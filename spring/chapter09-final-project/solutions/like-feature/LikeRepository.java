package com.edu.board.repository;

import com.edu.board.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 좋아요 리포지토리 — [도전 과제 참고 답안]
 *
 * 메서드 이름만으로 쿼리가 자동 생성되는 Spring Data JPA의 기능을 활용합니다.
 */
public interface LikeRepository extends JpaRepository<Like, Long> {

    /** 특정 사용자가 특정 게시글에 누른 좋아요 조회 (토글 시 존재 여부 확인용) */
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);

    /** 특정 사용자가 특정 게시글에 이미 좋아요를 눌렀는지 여부 */
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    /** 특정 게시글의 좋아요 개수 (count 쿼리 자동 생성) */
    long countByPostId(Long postId);
}
