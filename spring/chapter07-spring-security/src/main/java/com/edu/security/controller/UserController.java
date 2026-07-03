package com.edu.security.controller;

import com.edu.security.entity.User;
import com.edu.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 사용자 컨트롤러
 *
 * 인증된 사용자 정보 조회 및 관리자 전용 API를 제공합니다.
 *
 * - /api/users/me: 인증된 모든 사용자가 접근 가능
 * - /api/admin/users: ADMIN 역할만 접근 가능 (SecurityConfig에서 설정)
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    /**
     * 현재 로그인한 사용자 정보 조회
     *
     * GET /api/users/me
     * Authorization: Bearer {JWT_TOKEN}
     *
     * @AuthenticationPrincipal 어노테이션으로 SecurityContext에서
     * 현재 인증된 사용자(UserDetails) 객체를 주입받습니다.
     *
     * @param user 현재 인증된 사용자 (JWT 필터에서 설정됨)
     * @return 사용자 정보 (비밀번호 제외)
     */
    @GetMapping("/api/users/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal User user) {
        // 비밀번호는 응답에서 제외
        Map<String, Object> userInfo = Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole().name()
        );
        return ResponseEntity.ok(userInfo);
    }

    /**
     * [관리자 전용] 전체 사용자 목록 조회
     *
     * GET /api/admin/users
     * Authorization: Bearer {ADMIN_JWT_TOKEN}
     *
     * SecurityConfig에서 /api/admin/** 경로는 hasRole("ADMIN")으로 설정되어
     * ADMIN 역할을 가진 사용자만 접근할 수 있습니다.
     * 일반 USER가 접근하면 403 Forbidden 응답이 반환됩니다.
     *
     * 추가로 @PreAuthorize("hasRole('ADMIN')")를 메서드에 직접 선언했다.
     * - URL 기반 규칙(SecurityConfig)과 메서드 기반 규칙(@PreAuthorize)은 함께 쓸 수 있다.
     * - @EnableMethodSecurity(SecurityConfig)가 켜져 있어야 동작한다.
     * - 메서드 보안은 컨트롤러뿐 아니라 서비스 계층에도 적용할 수 있어 더 세밀한 제어가 가능하다.
     *
     * @return 전체 사용자 목록 (비밀번호 제외)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/admin/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(user -> Map.<String, Object>of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole().name()
                ))
                .toList();
        return ResponseEntity.ok(users);
    }
}
