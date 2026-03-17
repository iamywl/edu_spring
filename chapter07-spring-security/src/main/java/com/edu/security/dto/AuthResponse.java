package com.edu.security.dto;

/**
 * 인증 응답 DTO (Record)
 *
 * 회원가입 또는 로그인 성공 시 JWT 토큰을 반환합니다.
 *
 * @param token JWT 토큰 문자열
 */
public record AuthResponse(
        String token
) {
}
