package com.edu.security.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청 DTO (Record)
 *
 * @param username 사용자명
 * @param password 비밀번호
 */
public record LoginRequest(
        @NotBlank(message = "사용자명은 필수입니다")
        String username,

        @NotBlank(message = "비밀번호는 필수입니다")
        String password
) {
}
