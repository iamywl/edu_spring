package com.edu.jpa.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 팀(Team) 엔티티
 * - Member와 1:N(일대다) 관계
 * - mappedBy: 연관관계의 주인이 아님을 표시 (Member.team 필드가 주인)
 * - cascade: 팀 저장/삭제 시 소속 회원도 함께 처리
 */
@Entity
@Table(name = "team")
public class Team {

    // 기본 키 - 자동 생성 (PostgreSQL SERIAL)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 팀 이름 - NOT NULL, 최대 100자
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 일대다(1:N) 관계 매핑
     * - mappedBy = "team": Member 엔티티의 team 필드가 연관관계의 주인
     * - cascade = CascadeType.ALL: 영속성 전이 (팀 저장 시 회원도 함께 저장)
     * - orphanRemoval = true: 고아 객체 자동 삭제
     */
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    // 기본 생성자 (JPA 스펙에서 필수)
    protected Team() {
    }

    public Team(String name) {
        this.name = name;
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

    public List<Member> getMembers() {
        return members;
    }

    /**
     * 연관관계 편의 메서드
     * - 양방향 관계에서 양쪽 모두에 값을 설정해주는 메서드
     * - team.addMember(member) 호출 시 member.setTeam(team)도 자동 처리
     */
    public void addMember(Member member) {
        members.add(member);
        member.setTeam(this);
    }

    /**
     * 연관관계 편의 메서드 - 회원 제거
     */
    public void removeMember(Member member) {
        members.remove(member);
        member.setTeam(null);
    }
}
