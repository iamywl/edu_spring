package com.edu.board.controller;

import com.edu.board.dto.CommentRequest;
import com.edu.board.dto.CommentResponse;
import com.edu.board.entity.User;
import com.edu.board.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 댓글 컨트롤러
 *
 * 게시글에 대한 댓글 CRUD API를 제공합니다.
 * URL이 /api/posts/{postId}/comments 형태로 게시글에 종속됩니다.
 */
@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 댓글 목록 조회 API
     *
     * GET /api/posts/{postId}/comments
     * 해당 게시글의 모든 댓글을 작성일 순으로 반환합니다.
     *
     * @param postId 게시글 ID
     * @return 댓글 목록
     */
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        List<CommentResponse> responses = commentService.getComments(postId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 댓글 작성 API (인증 필요)
     *
     * POST /api/posts/{postId}/comments
     * Authorization: Bearer {token}
     *
     * @param postId  게시글 ID
     * @param request 댓글 작성 요청 DTO
     * @param user    인증된 사용자
     * @return 작성된 댓글 정보
     */
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal User user) {

        CommentResponse response = commentService.createComment(postId, request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 댓글 삭제 API (작성자 또는 관리자만 가능)
     *
     * DELETE /api/posts/{postId}/comments/{commentId}
     * Authorization: Bearer {token}
     *
     * @param postId    게시글 ID
     * @param commentId 댓글 ID
     * @param user      인증된 사용자 (권한 확인용)
     * @return 204 No Content
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal User user) {

        commentService.deleteComment(postId, commentId, user);
        return ResponseEntity.noContent().build();
    }
}
