package com.edu.jpa.repository;

import com.edu.jpa.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 팀(Team) 리포지토리
 * - JpaRepository 상속으로 기본 CRUD 메서드 자동 제공
 */
public interface TeamRepository extends JpaRepository<Team, Long> {

    // 팀 이름으로 조회
    Optional<Team> findByName(String name);

    // 팀 이름 존재 여부 확인
    boolean existsByName(String name);
}
