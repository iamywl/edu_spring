package com.edu.jpa.dto;

import com.edu.jpa.entity.Team;

import java.util.List;

/**
 * 팀 응답 DTO (record)
 *
 * @param id          팀 ID
 * @param name        팀 이름
 * @param memberCount 소속 회원 수
 * @param members     소속 회원 목록
 */
public record TeamResponse(
        Long id,
        String name,
        int memberCount,
        List<MemberResponse> members
) {
    /**
     * Entity -> DTO 변환 정적 팩토리 메서드
     * - 팀 정보와 소속 회원 목록을 함께 반환
     */
    public static TeamResponse from(Team team) {
        List<MemberResponse> memberResponses = team.getMembers().stream()
                .map(MemberResponse::from)
                .toList();

        return new TeamResponse(
                team.getId(),
                team.getName(),
                memberResponses.size(),
                memberResponses
        );
    }

    /**
     * Entity -> DTO 변환 (회원 목록 제외)
     */
    public static TeamResponse withoutMembers(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getMembers().size(),
                List.of()
        );
    }
}
