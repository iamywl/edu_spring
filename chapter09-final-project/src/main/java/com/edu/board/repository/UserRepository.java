package com.edu.board.repository;

import com.edu.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 리포지토리
 *
 * Spring Data JPA가 자동으로 구현체를 생성합니다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /** 사용자명으로 사용자 조회 (로그인, 인증에 사용) */
    Optional<User> findByUsername(String username);

    /** 사용자명 존재 여부 확인 (회원가입 시 중복 체크) */
    boolean existsByUsername(String username);
}
