package com.edu.board.repository;

import com.edu.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 게시글 리포지토리
 *
 * 페이징과 키워드 검색을 지원합니다.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    // === N+1 문제 해결: 작성자(author)를 한 번에 함께 조회 ===

    /**
     * 제목에 키워드가 포함된 게시글을 페이징하여 조회 (author 함께 로딩)
     *
     * <p><b>N+1 문제</b>: Post.author가 LAZY이므로, 목록을 조회한 뒤
     * 각 게시글의 작성자명(PostListResponse에서 post.getAuthor().getUsername())에
     * 접근하면 게시글 수(N)만큼 작성자 조회 쿼리가 추가로 나간다. (1 + N 쿼리)
     *
     * <p>@EntityGraph(attributePaths = "author")는 이 메서드를 실행할 때
     * author를 LEFT JOIN으로 함께 가져오도록 지시한다. → 쿼리 1번으로 해결.
     * post→author는 다대일(ToOne) 관계라 페이징과 함께 써도 안전하다.
     */
    @EntityGraph(attributePaths = "author")
    Page<Post> findByTitleContaining(String keyword, Pageable pageable);

    /**
     * 게시글 목록 전체 조회 (author 함께 로딩)
     *
     * <p>findByTitleContaining과 동일한 이유로 @EntityGraph를 붙여
     * 작성자명 접근 시 N+1이 발생하지 않도록 한다.
     */
    @Override
    @EntityGraph(attributePaths = "author")
    Page<Post> findAll(Pageable pageable);

    /** 특정 작성자의 게시글 목록 조회 */
    List<Post> findByAuthorUsername(String username);
}
