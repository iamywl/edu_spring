package com.edu.jpa.service;

import com.edu.jpa.dto.*;
import com.edu.jpa.entity.Member;
import com.edu.jpa.entity.Team;
import com.edu.jpa.exception.DuplicateResourceException;
import com.edu.jpa.exception.ResourceNotFoundException;
import com.edu.jpa.repository.MemberRepository;
import com.edu.jpa.repository.TeamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 회원(Member) 서비스
 * - 비즈니스 로직 처리
 * - @Transactional: 트랜잭션 관리 (메서드 단위로 트랜잭션 적용)
 * - readOnly = true: 읽기 전용 트랜잭션 (성능 최적화)
 */
@Service
@Transactional(readOnly = true) // 클래스 레벨: 기본적으로 읽기 전용
public class MemberService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    // 생성자 주입 (Spring 권장 방식)
    public MemberService(MemberRepository memberRepository, TeamRepository teamRepository) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
    }

    // === 회원 CRUD ===

    /**
     * 회원 등록
     * - @Transactional: 쓰기 작업이므로 readOnly = false (기본값)
     */
    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        // 이메일 중복 검사 (중복 → 409 Conflict)
        memberRepository.findByEmail(request.email())
                .ifPresent(m -> {
                    throw new DuplicateResourceException("이미 사용 중인 이메일입니다: " + request.email());
                });

        // 팀 조회 (teamId가 있는 경우, 없는 팀 → 404 Not Found)
        Team team = null;
        if (request.teamId() != null) {
            team = teamRepository.findById(request.teamId())
                    .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 팀입니다: " + request.teamId()));
        }

        // 회원 생성 및 저장
        Member member = new Member(request.name(), request.email(), team);
        Member savedMember = memberRepository.save(member);

        return MemberResponse.from(savedMember);
    }

    /**
     * 회원 단건 조회
     */
    public MemberResponse getMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 회원입니다: " + id));
        return MemberResponse.from(member);
    }

    /**
     * 회원 전체 조회 (페이징)
     * - Pageable: 페이지 번호, 크기, 정렬 정보를 담은 객체
     * - Page<T>: 페이징 결과 (데이터 + 메타정보)
     */
    public Page<MemberResponse> getMembers(Pageable pageable) {
        // findAll(pageable)은 @EntityGraph로 team을 함께 조회하므로
        // MemberResponse 변환 시 팀 이름 접근에서 N+1이 발생하지 않는다.
        return memberRepository.findAll(pageable)
                .map(MemberResponse::from); // Page의 map 메서드로 DTO 변환
    }

    /**
     * 회원 수정
     */
    @Transactional
    public MemberResponse updateMember(Long id, MemberRequest request) {
        // 수정할 회원 조회 (없으면 → 404)
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 회원입니다: " + id));

        // 이메일 중복 검사 (자신 제외, 중복 → 409)
        memberRepository.findByEmail(request.email())
                .filter(m -> !m.getId().equals(id))
                .ifPresent(m -> {
                    throw new DuplicateResourceException("이미 사용 중인 이메일입니다: " + request.email());
                });

        // 팀 조회 (teamId가 있는 경우, 없는 팀 → 404)
        Team team = null;
        if (request.teamId() != null) {
            team = teamRepository.findById(request.teamId())
                    .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 팀입니다: " + request.teamId()));
        }

        // 엔티티 수정 (JPA 변경 감지 - Dirty Checking)
        // save()를 명시적으로 호출하지 않아도 트랜잭션 커밋 시 변경사항이 자동 반영됨
        member.setName(request.name());
        member.setEmail(request.email());
        member.setTeam(team);

        return MemberResponse.from(member);
    }

    /**
     * 회원 삭제
     */
    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("존재하지 않는 회원입니다: " + id);
        }
        memberRepository.deleteById(id);
    }

    // === 검색 ===

    /**
     * 이름으로 회원 검색 (페이징)
     * - 쿼리 메서드 사용: findByNameContaining
     */
    public Page<MemberResponse> searchByName(String keyword, Pageable pageable) {
        return memberRepository.findByNameContaining(keyword, pageable)
                .map(MemberResponse::from);
    }

    /**
     * 이메일 도메인으로 회원 검색
     * - Native Query 사용
     */
    public List<MemberResponse> searchByEmailDomain(String domain) {
        return memberRepository.findByEmailDomain(domain).stream()
                .map(MemberResponse::from)
                .toList();
    }

    /**
     * 팀별 회원 조회
     * - 쿼리 메서드 사용: findByTeamName
     */
    public List<MemberResponse> getMembersByTeam(String teamName) {
        return memberRepository.findByTeamName(teamName).stream()
                .map(MemberResponse::from)
                .toList();
    }

    // === 팀 CRUD ===

    /**
     * 팀 생성
     */
    @Transactional
    public TeamResponse createTeam(TeamRequest request) {
        if (teamRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("이미 존재하는 팀 이름입니다: " + request.name());
        }
        Team team = new Team(request.name());
        Team savedTeam = teamRepository.save(team);
        return TeamResponse.withoutMembers(savedTeam);
    }

    /**
     * 팀 단건 조회 (소속 회원 포함)
     * - findByIdWithMembers: members를 JOIN FETCH로 함께 로딩 (N+1 방지)
     */
    public TeamResponse getTeam(Long id) {
        Team team = teamRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 팀입니다: " + id));
        return TeamResponse.from(team);
    }

    /**
     * 팀별 소속 회원 목록 조회
     * - GET /api/teams/{id}/members 전용: 팀 정보가 아닌 "회원 목록"만 반환한다.
     */
    public List<MemberResponse> getTeamMembers(Long id) {
        Team team = teamRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 팀입니다: " + id));
        return team.getMembers().stream()
                .map(MemberResponse::from)
                .toList();
    }

    /**
     * 팀 전체 조회
     * - findAllWithMembers: 각 팀의 members를 함께 로딩해 회원 수 집계 시 N+1 방지
     */
    public List<TeamResponse> getTeams() {
        return teamRepository.findAllWithMembers().stream()
                .map(TeamResponse::withoutMembers)
                .toList();
    }

    /**
     * 팀 삭제
     */
    @Transactional
    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("존재하지 않는 팀입니다: " + id);
        }
        teamRepository.deleteById(id);
    }
}
