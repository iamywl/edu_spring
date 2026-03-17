package com.edu.board.repository;

import com.edu.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 댓글 리포지토리
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /** 특정 게시글의 댓글 목록을 작성일 순으로 조회 */
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
