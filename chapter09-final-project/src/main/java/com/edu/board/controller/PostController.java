package com.edu.board.controller;

import com.edu.board.dto.*;
import com.edu.board.entity.User;
import com.edu.board.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 게시글 컨트롤러
 *
 * 게시글 CRUD, 페이징, 검색 API를 제공합니다.
 * - 조회(GET)는 인증 없이 가능
 * - 작성/수정/삭제는 인증 필요
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * 게시글 목록 조회 API (페이징, 검색 지원)
     *
     * GET /api/posts?page=0&size=10&keyword=검색어
     *
     * @param page    페이지 번호 (기본값: 0)
     * @param size    페이지 크기 (기본값: 10)
     * @param keyword 검색 키워드 (선택)
     * @return 페이징된 게시글 목록
     */
    @GetMapping
    public ResponseEntity<PageResponse<PostListResponse>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        PageResponse<PostListResponse> response = postService.getPosts(page, size, keyword);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회 API
     *
     * GET /api/posts/{id}
     * 조회 시 조회수가 1 증가합니다.
     *
     * @param id 게시글 ID
     * @return 게시글 상세 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        PostResponse response = postService.getPost(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 작성 API (인증 필요)
     *
     * POST /api/posts
     * Authorization: Bearer {token}
     *
     * @param request 게시글 작성 요청 DTO
     * @param user    인증된 사용자 (자동 주입)
     * @return 작성된 게시글 정보
     */
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal User user) {

        PostResponse response = postService.createPost(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 게시글 수정 API (작성자만 가능)
     *
     * PUT /api/posts/{id}
     * Authorization: Bearer {token}
     *
     * @param id      게시글 ID
     * @param request 수정 요청 DTO
     * @param user    인증된 사용자 (작성자 확인용)
     * @return 수정된 게시글 정보
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal User user) {

        PostResponse response = postService.updatePost(id, request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 삭제 API (작성자 또는 관리자만 가능)
     *
     * DELETE /api/posts/{id}
     * Authorization: Bearer {token}
     *
     * @param id   게시글 ID
     * @param user 인증된 사용자 (권한 확인용)
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        postService.deletePost(id, user);
        return ResponseEntity.noContent().build();
    }
}
