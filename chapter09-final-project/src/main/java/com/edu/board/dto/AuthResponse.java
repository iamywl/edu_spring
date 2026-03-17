package com.edu.board.dto;

/**
 * 인증 응답 DTO (로그인 성공 시 반환)
 *
 * @param token    JWT 액세스 토큰
 * @param username 인증된 사용자명
 */
public record AuthResponse(
        String token,
        String username
) {
}
