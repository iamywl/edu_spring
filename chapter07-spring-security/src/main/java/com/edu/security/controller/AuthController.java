package com.edu.security.controller;

import com.edu.security.dto.AuthResponse;
import com.edu.security.dto.LoginRequest;
import com.edu.security.dto.SignUpRequest;
import com.edu.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 컨트롤러
 *
 * 회원가입과 로그인 API를 제공합니다.
 * /api/auth/** 경로는 SecurityConfig에서 permitAll()로 설정되어
 * 인증 없이 접근할 수 있습니다.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 API
     *
     * POST /api/auth/signup
     *
     * 요청 예시:
     * {
     *   "username": "testuser",
     *   "password": "password123",
     *   "role": "USER"
     * }
     *
     * @param request 회원가입 요청 DTO
     * @return JWT 토큰이 포함된 응답 (201 Created)
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인 API
     *
     * POST /api/auth/login
     *
     * 요청 예시:
     * {
     *   "username": "testuser",
     *   "password": "password123"
     * }
     *
     * @param request 로그인 요청 DTO
     * @return JWT 토큰이 포함된 응답 (200 OK)
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
