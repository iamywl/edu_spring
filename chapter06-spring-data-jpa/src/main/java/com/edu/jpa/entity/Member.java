package com.edu.jpa.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 회원(Member) 엔티티
 * - @Entity: JPA가 관리하는 엔티티 클래스임을 선언
 * - @Table: 매핑할 데이터베이스 테이블명 지정
 * - @EntityListeners: 엔티티 이벤트 리스너 등록 (Auditing용)
 */
@Entity
@Table(name = "member")
@EntityListeners(AuditingEntityListener.class) // @CreatedDate 사용을 위해 필요
public class Member {

    // 기본 키(PK) - 자동 생성 전략: IDENTITY (PostgreSQL의 SERIAL)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이름 - NOT NULL, 최대 50자
    @Column(nullable = false, length = 50)
    private String name;

    // 이메일 - NOT NULL, 유니크 제약조건
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 생성 일시 - JPA Auditing으로 자동 기록
     * - @CreatedDate: 엔티티가 처음 저장될 때 현재 시각 자동 설정
     * - updatable = false: 수정 시 변경되지 않도록 설정
     */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * 다대일(N:1) 관계 - 여러 회원이 하나의 팀에 소속
     * - @ManyToOne: N:1 관계 매핑
     * - fetch = FetchType.LAZY: 지연 로딩 (팀 정보가 필요할 때만 쿼리 실행)
     * - @JoinColumn: 외래 키(FK) 컬럼명 지정
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // 기본 생성자 (JPA 스펙에서 필수 - protected로 외부 직접 호출 방지)
    protected Member() {
    }

    // 생성자 - 필수 필드만
    public Member(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // 생성자 - 팀 포함
    public Member(String name, String email, Team team) {
        this.name = name;
        this.email = email;
        this.team = team;
    }

    // --- Getter / Setter ---

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
