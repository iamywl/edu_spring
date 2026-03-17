package com.edu.board.dto;

import com.edu.board.entity.Comment;

import java.time.LocalDateTime;

/**
 * 댓글 응답 DTO
 *
 * @param id        댓글 ID
 * @param content   댓글 내용
 * @param author    작성자명
 * @param createdAt 작성 일시
 */
public record CommentResponse(
        Long id,
        String content,
        String author,
        LocalDateTime createdAt
) {
    /** Comment 엔티티를 CommentResponse DTO로 변환하는 팩토리 메서드 */
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getUsername(),
                comment.getCreatedAt()
        );
    }
}
