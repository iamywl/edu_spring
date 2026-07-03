package com.edu.jpa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 팀 생성/수정 요청 DTO (record)
 *
 * @param name 팀 이름 (필수, 2~100자)
 */
public record TeamRequest(
        @NotBlank(message = "팀 이름은 필수입니다")
        @Size(min = 2, max = 100, message = "팀 이름은 2자 이상 100자 이하여야 합니다")
        String name
) {
}
