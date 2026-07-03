package com.edu.security.repository;

import com.edu.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 리포지토리
 *
 * Spring Data JPA가 인터페이스를 기반으로 구현체를 자동 생성합니다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 사용자를 조회합니다.
     * Spring Security의 UserDetailsService에서 사용자 로드 시 호출됩니다.
     *
     * @param username 사용자명
     * @return 사용자 (Optional)
     */
    Optional<User> findByUsername(String username);

    /**
     * 사용자명 중복 여부를 확인합니다.
     *
     * @param username 사용자명
     * @return 존재 여부
     */
    boolean existsByUsername(String username);
}
