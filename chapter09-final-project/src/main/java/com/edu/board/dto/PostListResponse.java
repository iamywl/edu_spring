package com.edu.board.dto;

import com.edu.board.entity.Post;

import java.time.LocalDateTime;

/**
 * 게시글 목록용 응답 DTO (본문 미포함 - 목록 조회 시 경량화)
 *
 * @param id           게시글 ID
 * @param title        제목
 * @param author       작성자명
 * @param viewCount    조회수
 * @param commentCount 댓글 수
 * @param createdAt    작성 일시
 */
public record PostListResponse(
        Long id,
        String title,
        String author,
        int viewCount,
        int commentCount,
        LocalDateTime createdAt
) {
    /** Post 엔티티를 PostListResponse DTO로 변환하는 팩토리 메서드 */
    public static PostListResponse from(Post post) {
        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getAuthor().getUsername(),
                post.getViewCount(),
                post.getComments().size(),
                post.getCreatedAt()
        );
    }
}
