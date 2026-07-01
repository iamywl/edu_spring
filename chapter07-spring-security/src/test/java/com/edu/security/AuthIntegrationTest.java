package com.edu.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 인증/인가 통합 테스트
 *
 * 실제 애플리케이션 컨텍스트를 띄워(@SpringBootTest) 회원가입 → 로그인 → 보호된 API 호출의
 * 전체 흐름을 검증한다. H2 인메모리 DB(src/test/resources/application.yml)를 사용하므로
 * PostgreSQL 없이도 실행된다.
 *
 * 핵심 검증 포인트:
 * - 토큰 없이 보호된 API 호출 → 401 Unauthorized (인증 실패)
 * - 발급받은 토큰으로 호출 → 200 OK
 * - 일반 USER가 ADMIN 전용 API 호출 → 403 Forbidden (인가 실패)
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 → 로그인 → 토큰으로 보호된 API 호출 전체 흐름")
    void signupLoginAndAccessProtectedApi() throws Exception {
        // given - 회원가입
        String signupBody = """
                {"username":"alice","password":"password123","role":"USER"}
                """;
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupBody))
                .andExpect(status().isCreated());

        // when - 로그인하여 토큰 발급
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"alice","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = loginJson.get("token").asText();

        // then - 토큰으로 /api/users/me 호출 → 200
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    @DisplayName("토큰 없이 보호된 API 호출 → 401 Unauthorized")
    void accessProtectedApiWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("일반 USER가 ADMIN 전용 API 호출 → 403 Forbidden")
    void userCannotAccessAdminApi() throws Exception {
        // given - USER 권한으로 가입/로그인
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"bob","password":"password123","role":"USER"}
                                """))
                .andExpect(status().isCreated());

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"bob","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andReturn();
        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("token").asText();

        // when & then - ADMIN 전용 API 접근 → 403
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
