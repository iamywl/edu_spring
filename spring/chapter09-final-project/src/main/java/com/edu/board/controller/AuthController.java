package com.edu.board.controller;

import com.edu.board.dto.AuthResponse;
import com.edu.board.dto.LoginRequest;
import com.edu.board.dto.SignUpRequest;
import com.edu.board.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 컨트롤러
 *
 * 회원가입과 로그인 API를 제공합니다.
 * 인증 없이 접근 가능한 공개 API입니다.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 회원가입 API
     *
     * POST /api/auth/signup
     * 요청 본문: { "username": "...", "password": "..." }
     * 응답: JWT 토큰과 사용자명
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
     * 요청 본문: { "username": "...", "password": "..." }
     * 응답: JWT 토큰과 사용자명
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
