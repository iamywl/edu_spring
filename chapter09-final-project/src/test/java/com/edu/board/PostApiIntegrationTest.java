package com.edu.board;

import com.edu.board.dto.*;
import com.edu.board.entity.Post;
import com.edu.board.entity.User;
import com.edu.board.repository.PostRepository;
import com.edu.board.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 게시글 API 통합 테스트
 *
 * Testcontainers를 사용하여 실제 PostgreSQL 컨테이너에서 테스트합니다.
 * 전체 Spring Boot 애플리케이션 컨텍스트를 로드하고,
 * MockMvc로 HTTP 요청을 시뮬레이션합니다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PostApiIntegrationTest {

    /** Testcontainers - PostgreSQL 컨테이너 (테스트 전체에서 공유) */
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("edu_spring_test")
            .withUsername("test")
            .withPassword("test1234");

    /** Testcontainers의 동적 포트를 Spring DataSource에 연결 */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** 테스트에서 사용할 JWT 토큰 (로그인 후 저장) */
    private static String authToken;

    /**
     * 테스트 1: 회원가입 테스트
     * - POST /api/auth/signup
     * - 201 Created 응답과 JWT 토큰을 확인합니다.
     */
    @Test
    @Order(1)
    @DisplayName("회원가입 - 성공")
    void signUp_Success() throws Exception {
        SignUpRequest request = new SignUpRequest("testuser", "password123");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    /**
     * 테스트 2: 중복 사용자명으로 회원가입 실패
     * - 같은 사용자명으로 두 번 가입 시도
     * - 400 Bad Request 응답을 확인합니다.
     */
    @Test
    @Order(2)
    @DisplayName("회원가입 - 중복 사용자명 실패")
    void signUp_DuplicateUsername() throws Exception {
        SignUpRequest request = new SignUpRequest("testuser", "password456");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 테스트 3: 로그인 테스트
     * - POST /api/auth/login
     * - JWT 토큰을 받아서 이후 테스트에서 사용합니다.
     */
    @Test
    @Order(3)
    @DisplayName("로그인 - 성공 및 토큰 발급")
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andReturn();

        // 토큰을 저장하여 이후 테스트에서 사용
        String responseBody = result.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);
        authToken = authResponse.token();
    }

    /**
     * 테스트 4: 게시글 작성 테스트
     * - POST /api/posts (인증 필요)
     * - 201 Created 응답과 게시글 정보를 확인합니다.
     */
    @Test
    @Order(4)
    @DisplayName("게시글 작성 - 인증된 사용자")
    void createPost_Authenticated() throws Exception {
        PostRequest request = new PostRequest("테스트 게시글", "테스트 내용입니다.");

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("테스트 게시글"))
                .andExpect(jsonPath("$.content").value("테스트 내용입니다."))
                .andExpect(jsonPath("$.author").value("testuser"))
                .andExpect(jsonPath("$.viewCount").value(0));
    }

    /**
     * 테스트 5: 인증 없이 게시글 작성 시도
     * - 401 Unauthorized 응답을 확인합니다.
     */
    @Test
    @Order(5)
    @DisplayName("게시글 작성 - 인증 없이 실패")
    void createPost_Unauthenticated() throws Exception {
        PostRequest request = new PostRequest("인증 없는 게시글", "실패해야 합니다.");

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    /**
     * 테스트 6: 게시글 상세 조회 테스트
     * - GET /api/posts/1 (인증 불필요)
     * - 조회수가 증가하는지 확인합니다.
     */
    @Test
    @Order(6)
    @DisplayName("게시글 상세 조회 - 조회수 증가")
    void getPost_ViewCountIncrement() throws Exception {
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("테스트 게시글"))
                .andExpect(jsonPath("$.viewCount").value(1)); // 조회수 1 증가
    }

    /**
     * 테스트 7: 게시글 목록 조회 (페이징)
     * - GET /api/posts?page=0&size=10
     */
    @Test
    @Order(7)
    @DisplayName("게시글 목록 조회 - 페이징")
    void getPosts_Paging() throws Exception {
        // 추가 게시글 작성
        for (int i = 2; i <= 5; i++) {
            PostRequest request = new PostRequest("게시글 " + i, "내용 " + i);
            mockMvc.perform(post("/api/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + authToken)
                    .content(objectMapper.writeValueAsString(request)));
        }

        mockMvc.perform(get("/api/posts")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true));
    }

    /**
     * 테스트 8: 게시글 검색
     * - GET /api/posts?keyword=테스트
     */
    @Test
    @Order(8)
    @DisplayName("게시글 검색 - 키워드")
    void getPosts_Search() throws Exception {
        mockMvc.perform(get("/api/posts")
                        .param("keyword", "테스트"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("테스트 게시글"));
    }

    /**
     * 테스트 9: 게시글 수정
     * - PUT /api/posts/1
     */
    @Test
    @Order(9)
    @DisplayName("게시글 수정 - 작성자")
    void updatePost_ByAuthor() throws Exception {
        PostRequest request = new PostRequest("수정된 제목", "수정된 내용");

        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.content").value("수정된 내용"));
    }

    /**
     * 테스트 10: 존재하지 않는 게시글 조회
     * - GET /api/posts/999
     * - 404 Not Found 응답을 확인합니다.
     */
    @Test
    @Order(10)
    @DisplayName("존재하지 않는 게시글 조회 - 404")
    void getPost_NotFound() throws Exception {
        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * 테스트 11: 댓글 작성
     * - POST /api/posts/1/comments
     */
    @Test
    @Order(11)
    @DisplayName("댓글 작성 - 성공")
    void createComment_Success() throws Exception {
        CommentRequest request = new CommentRequest("좋은 글이네요!");

        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("좋은 글이네요!"))
                .andExpect(jsonPath("$.author").value("testuser"));
    }

    /**
     * 테스트 12: 댓글 목록 조회
     * - GET /api/posts/1/comments
     */
    @Test
    @Order(12)
    @DisplayName("댓글 목록 조회")
    void getComments_Success() throws Exception {
        mockMvc.perform(get("/api/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content").value("좋은 글이네요!"));
    }

    /**
     * 테스트 13: 게시글 삭제 (작성자)
     * - DELETE /api/posts/5
     */
    @Test
    @Order(13)
    @DisplayName("게시글 삭제 - 작성자")
    void deletePost_ByAuthor() throws Exception {
        mockMvc.perform(delete("/api/posts/5")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNoContent());

        // 삭제 확인
        mockMvc.perform(get("/api/posts/5"))
                .andExpect(status().isNotFound());
    }

    /**
     * 테스트 14: 유효성 검증 실패 - 빈 제목
     * - 400 Bad Request 응답을 확인합니다.
     */
    @Test
    @Order(14)
    @DisplayName("유효성 검증 - 빈 제목 실패")
    void createPost_ValidationFail() throws Exception {
        PostRequest request = new PostRequest("", "내용은 있습니다.");

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
