package com.edu.jpa.controller;

import com.edu.jpa.dto.*;
import com.edu.jpa.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 회원(Member) REST API 컨트롤러
 * - @RestController: JSON 응답 반환
 * - @RequestMapping: 공통 URL 접두사
 */
@RestController
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // ========================
    // 회원 API
    // ========================

    /**
     * 회원 등록
     * POST /api/members
     * - @Valid: 요청 DTO의 유효성 검증 실행
     * - @RequestBody: JSON 요청 본문을 DTO로 변환
     */
    @PostMapping("/members")
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberRequest request) {
        MemberResponse response = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 회원 단건 조회
     * GET /api/members/{id}
     * - @PathVariable: URL 경로의 변수 바인딩
     */
    @GetMapping("/members/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMember(id));
    }

    /**
     * 회원 전체 조회 (페이징)
     * GET /api/members?page=0&size=10&sort=createdAt,desc
     *
     * - @PageableDefault: 페이징 기본값 설정
     *   - size: 페이지당 항목 수 (기본 10)
     *   - sort: 정렬 기준 필드
     *   - direction: 정렬 방향
     * - Pageable: Spring이 요청 파라미터(page, size, sort)를 자동 바인딩
     */
    @GetMapping("/members")
    public ResponseEntity<Page<MemberResponse>> getMembers(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(memberService.getMembers(pageable));
    }

    /**
     * 회원 수정
     * PUT /api/members/{id}
     */
    @PutMapping("/members/{id}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request) {
        return ResponseEntity.ok(memberService.updateMember(id, request));
    }

    /**
     * 회원 삭제
     * DELETE /api/members/{id}
     */
    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 이름으로 회원 검색 (페이징)
     * GET /api/members/search?keyword=홍&page=0&size=10
     * - @RequestParam: 쿼리 파라미터 바인딩
     */
    @GetMapping("/members/search")
    public ResponseEntity<Page<MemberResponse>> searchByName(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(memberService.searchByName(keyword, pageable));
    }

    /**
     * 이메일 도메인으로 회원 검색
     * GET /api/members/search/email?domain=example.com
     */
    @GetMapping("/members/search/email")
    public ResponseEntity<List<MemberResponse>> searchByEmailDomain(@RequestParam String domain) {
        return ResponseEntity.ok(memberService.searchByEmailDomain(domain));
    }

    // ========================
    // 팀 API
    // ========================

    /**
     * 팀 생성
     * POST /api/teams
     */
    @PostMapping("/teams")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest request) {
        TeamResponse response = memberService.createTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 팀 전체 조회
     * GET /api/teams
     */
    @GetMapping("/teams")
    public ResponseEntity<List<TeamResponse>> getTeams() {
        return ResponseEntity.ok(memberService.getTeams());
    }

    /**
     * 팀 단건 조회 (소속 회원 포함)
     * GET /api/teams/{id}
     */
    @GetMapping("/teams/{id}")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getTeam(id));
    }

    /**
     * 팀별 회원 조회
     * GET /api/teams/{id}/members
     */
    @GetMapping("/teams/{id}/members")
    public ResponseEntity<TeamResponse> getTeamMembers(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getTeam(id));
    }

    /**
     * 팀 삭제
     * DELETE /api/teams/{id}
     */
    @DeleteMapping("/teams/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        memberService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    // ========================
    // 예외 처리
    // ========================

    /**
     * IllegalArgumentException 처리
     * - 존재하지 않는 리소스, 중복 데이터 등
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
    }
}
