package com.edu.board.repository;

import com.edu.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 댓글 리포지토리
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /** 특정 게시글의 댓글 목록을 작성일 순으로 조회 */
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    // === N+1 문제 해결: 댓글 수를 COUNT 쿼리 한 번으로 집계 ===

    /**
     * 여러 게시글의 댓글 수를 한 번의 쿼리로 집계한다.
     *
     * <p><b>N+1 문제</b>: 목록 DTO 변환 시 게시글마다 post.getComments().size()를
     * 호출하면, LAZY 컬렉션을 초기화하기 위해 게시글 수(N)만큼 댓글 조회 쿼리가
     * 추가로 나간다. 게다가 단순 개수만 필요한데도 댓글 전체를 메모리에 올린다.
     *
     * <p>대신 postId IN (...) 조건으로 GROUP BY 집계하면, 여러 게시글의 댓글 수를
     * COUNT 쿼리 <b>단 한 번</b>으로 구할 수 있다. 결과의 각 원소는
     * {@code [postId, count]} 형태의 배열이다.
     */
    @Query("SELECT c.post.id, COUNT(c) FROM Comment c WHERE c.post.id IN :postIds GROUP BY c.post.id")
    List<Object[]> countByPostIds(@Param("postIds") List<Long> postIds);
}
