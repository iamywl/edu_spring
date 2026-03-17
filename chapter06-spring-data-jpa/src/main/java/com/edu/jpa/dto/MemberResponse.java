package com.edu.jpa.dto;

import com.edu.jpa.entity.Member;

import java.time.LocalDateTime;

/**
 * 회원 응답 DTO (record)
 * - 엔티티를 직접 API 응답으로 노출하지 않고 DTO로 변환하여 반환
 * - 순환 참조 방지 (Member <-> Team 양방향 관계)
 *
 * @param id        회원 ID
 * @param name      회원 이름
 * @param email     이메일
 * @param teamName  소속 팀 이름 (팀 미소속 시 null)
 * @param createdAt 가입 일시
 */
public record MemberResponse(
        Long id,
        String name,
        String email,
        String teamName,
        LocalDateTime createdAt
) {
    /**
     * Entity -> DTO 변환 정적 팩토리 메서드
     * - 엔티티 객체를 응답 DTO로 변환
     */
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getTeam() != null ? member.getTeam().getName() : null,
                member.getCreatedAt()
        );
    }
}
