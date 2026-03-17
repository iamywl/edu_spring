package com.edu.board.repository;

import com.edu.board.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 게시글 리포지토리
 *
 * 페이징과 키워드 검색을 지원합니다.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /** 제목에 키워드가 포함된 게시글을 페이징하여 조회 */
    Page<Post> findByTitleContaining(String keyword, Pageable pageable);

    /** 특정 작성자의 게시글 목록 조회 */
    List<Post> findByAuthorUsername(String username);
}
