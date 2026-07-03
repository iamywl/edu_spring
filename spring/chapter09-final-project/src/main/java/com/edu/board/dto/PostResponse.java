package com.edu.board.dto;

import com.edu.board.entity.Post;

import java.time.LocalDateTime;

/**
 * 게시글 상세 응답 DTO (본문 포함)
 *
 * @param id           게시글 ID
 * @param title        제목
 * @param content      본문 내용
 * @param author       작성자명
 * @param viewCount    조회수
 * @param commentCount 댓글 수
 * @param createdAt    작성 일시
 */
public record PostResponse(
        Long id,
        String title,
        String content,
        String author,
        int viewCount,
        int commentCount,
        LocalDateTime createdAt
) {
    /** Post 엔티티를 PostResponse DTO로 변환하는 팩토리 메서드 */
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getUsername(),
                post.getViewCount(),
                post.getComments().size(),
                post.getCreatedAt()
        );
    }
}
