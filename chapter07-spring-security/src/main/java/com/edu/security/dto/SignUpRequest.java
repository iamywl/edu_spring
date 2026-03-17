package com.edu.security.dto;

import com.edu.security.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 요청 DTO (Record)
 *
 * Java 16+의 Record를 사용하여 불변(immutable) 데이터 클래스를 간결하게 정의합니다.
 * - 자동으로 생성자, getter, equals(), hashCode(), toString() 생성
 * - Bean Validation 어노테이션으로 입력값 검증
 *
 * @param username 사용자명 (2~50자)
 * @param password 비밀번호 (8자 이상)
 * @param role     역할 (USER 또는 ADMIN)
 */
public record SignUpRequest(
        @NotBlank(message = "사용자명은 필수입니다")
        @Size(min = 2, max = 50, message = "사용자명은 2~50자여야 합니다")
        String username,

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
        String password,

        @NotNull(message = "역할은 필수입니다")
        Role role
) {
}
