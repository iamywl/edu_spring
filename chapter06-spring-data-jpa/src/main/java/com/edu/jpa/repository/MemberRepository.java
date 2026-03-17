package com.edu.jpa.repository;

import com.edu.jpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<Member> findByNameContaining(String name, Pageable pageable);
}
