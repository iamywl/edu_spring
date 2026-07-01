package com.edu.jpa.repository;

import com.edu.jpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 회원(Member) 리포지토리
 * - JpaRepository<Member, Long>: Member 엔티티, 기본 키 타입 Long
 * - 기본 CRUD 메서드 자동 제공: save(), findById(), findAll(), delete() 등
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    // === 쿼리 메서드: 메서드 이름으로 쿼리 자동 생성 ===

    // 이메일로 회원 조회 (Optional: 결과가 없을 수 있음)
    Optional<Member> findByEmail(String email);

    // 이름에 특정 문자열이 포함된 회원 조회 (LIKE '%name%')
    List<Member> findByNameContaining(String name);

    // 팀 이름으로 회원 조회 (연관 엔티티의 필드로 조회)
    List<Member> findByTeamName(String teamName);

    // === JPQL: 엔티티 기반 쿼리 ===

    // JPQL - 이름 검색 (엔티티명 Member, 필드명 name 사용)
    @Query("SELECT m FROM Member m WHERE m.name LIKE %:keyword%")
    List<Member> searchByName(@Param("keyword") String keyword);

    // === Native Query: 직접 SQL 실행 ===

    // Native Query - 이메일 도메인으로 검색 (실제 테이블명, 컬럼명 사용)
    @Query(value = "SELECT * FROM member WHERE email LIKE %:domain", nativeQuery = true)
    List<Member> findByEmailDomain(@Param("domain") String domain);

    // === 페이징: Pageable 파라미터 추가 ===

    // 이름 검색 + 페이징 (Page 객체 반환)
    // @EntityGraph로 team을 함께 조회해 검색 결과에서도 N+1을 방지한다.
    @EntityGraph(attributePaths = "team")
    Page<Member> findByNameContaining(String name, Pageable pageable);

    // === N+1 문제 해결: 연관 엔티티(team)를 한 번에 함께 조회 ===

    /**
     * 회원 목록 조회 (team 함께 로딩) - @EntityGraph 방식
     *
     * <p><b>N+1 문제</b>: Member.team이 LAZY이므로, 회원 목록을 조회한 뒤
     * 각 회원의 팀 이름(MemberResponse에서 member.getTeam().getName())에 접근하면
     * 회원 수(N)만큼 팀 조회 쿼리가 추가로 나간다. (1 + N 쿼리)
     *
     * <p>@EntityGraph(attributePaths = "team")는 이 메서드를 실행할 때
     * team을 LEFT JOIN으로 함께 가져오도록 지시한다. → 쿼리 1번으로 해결.
     * member→team은 다대일(ToOne) 관계라 페이징과 함께 써도 안전하다.
     */
    @Override
    @EntityGraph(attributePaths = "team")
    Page<Member> findAll(Pageable pageable);

    /**
     * 회원 전체 조회 (team 함께 로딩) - JOIN FETCH(JPQL) 방식
     *
     * <p>@EntityGraph와 동일한 효과를 JPQL의 {@code JOIN FETCH}로 직접 작성한 예시.
     * 두 방식 모두 N+1을 막는 표준 해법이다.
     */
    @Query("SELECT m FROM Member m JOIN FETCH m.team")
    List<Member> findAllWithTeam();
}
