package com.edu.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 요청 DTO
 *
 * @param username 사용자명 (3~50자)
 * @param password 비밀번호 (6자 이상)
 */
public record SignUpRequest(
        @NotBlank(message = "사용자명은 필수입니다.")
        @Size(min = 3, max = 50, message = "사용자명은 3~50자여야 합니다.")
        String username,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다.")
        String password
) {
}
