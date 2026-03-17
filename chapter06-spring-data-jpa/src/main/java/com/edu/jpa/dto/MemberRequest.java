package com.edu.jpa.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 회원 생성/수정 요청 DTO (record)
 * - Java record: 불변 데이터 클래스 (getter, equals, hashCode, toString 자동 생성)
 * - @Valid와 함께 사용하여 입력값 검증
 *
 * @param name   회원 이름 (필수, 2~50자)
 * @param email  이메일 (필수, 이메일 형식)
 * @param teamId 소속 팀 ID (선택)
 */
public record MemberRequest(
        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다")
        String name,

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        Long teamId // 팀 ID (nullable - 팀 미소속 가능)
) {
}
