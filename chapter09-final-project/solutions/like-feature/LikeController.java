package com.edu.board.controller;

import com.edu.board.entity.User;
import com.edu.board.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 좋아요 컨트롤러 — [도전 과제 참고 답안]
 *
 * 게시글 좋아요 추가/취소 API를 제공합니다.
 * URL이 /api/posts/{postId}/likes 형태로 게시글에 종속됩니다.
 *
 * 인증: SecurityConfig에서 GET /api/posts/** 만 permitAll 이므로
 *       POST/DELETE /api/posts/{postId}/likes 는 자동으로 인증이 필요합니다.
 *       (별도 설정 변경 없이 토큰 없는 요청은 401을 받습니다.)
 */
@RestController
@RequestMapping("/api/posts/{postId}/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    /**
     * 좋아요 추가 API (인증 필요)
     *
     * POST /api/posts/{postId}/likes
     * Authorization: Bearer {token}
     *
     * @param postId 게시글 ID
     * @param user   인증된 사용자 (자동 주입)
     * @return 처리 후 좋아요 개수 ({"likeCount": N})
     */
    @PostMapping
    public ResponseEntity<Map<String, Long>> like(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user) {

        long likeCount = likeService.like(postId, user);
        return ResponseEntity.ok(Map.of("likeCount", likeCount));
    }

    /**
     * 좋아요 취소 API (인증 필요)
     *
     * DELETE /api/posts/{postId}/likes
     * Authorization: Bearer {token}
     *
     * @param postId 게시글 ID
     * @param user   인증된 사용자 (자동 주입)
     * @return 처리 후 좋아요 개수 ({"likeCount": N})
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Long>> unlike(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user) {

        long likeCount = likeService.unlike(postId, user);
        return ResponseEntity.ok(Map.of("likeCount", likeCount));
    }
}
