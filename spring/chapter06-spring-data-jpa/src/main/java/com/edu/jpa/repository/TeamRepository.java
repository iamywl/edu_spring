package com.edu.jpa.repository;

import com.edu.jpa.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    // === N+1 문제 해결: 소속 회원(members)을 JOIN FETCH로 함께 조회 ===

    /**
     * 팀 단건 조회 (소속 회원 함께 로딩)
     *
     * <p>TeamResponse.from()이 team.getMembers()를 순회하므로, 그냥 findById로 가져오면
     * 회원 목록 접근 시 별도 쿼리가 한 번 더 나간다. LEFT JOIN FETCH로 한 번에 가져온다.
     */
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.members WHERE t.id = :id")
    Optional<Team> findByIdWithMembers(@Param("id") Long id);

    /**
     * 팀 전체 조회 (각 팀의 소속 회원 함께 로딩)
     *
     * <p>팀 목록에서 팀마다 members.size()에 접근하면 팀 수(N)만큼 추가 쿼리가 나간다(N+1).
     * 컬렉션을 FETCH JOIN할 때는 중복 행이 생기므로 {@code DISTINCT}로 제거한다.
     */
    @Query("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.members")
    List<Team> findAllWithMembers();
}
