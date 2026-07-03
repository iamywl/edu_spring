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
    /**
     * Post 엔티티를 PostListResponse DTO로 변환하는 팩토리 메서드
     *
     * <p>댓글 수(commentCount)는 파라미터로 받는다. 예전에는
     * post.getComments().size()로 구했는데, 이 방식은 게시글마다 LAZY 컬렉션을
     * 초기화하면서 N+1 쿼리를 유발한다. 서비스에서 COUNT 쿼리로 한 번에 집계한 값을
     * 넘겨받아 사용함으로써 N+1을 제거했다.
     *
     * @param post         게시글 엔티티 (author는 @EntityGraph로 함께 로딩됨)
     * @param commentCount 미리 집계한 댓글 수
     */
    public static PostListResponse from(Post post, long commentCount) {
        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getAuthor().getUsername(),
                post.getViewCount(),
                (int) commentCount,
                post.getCreatedAt()
        );
    }
}
