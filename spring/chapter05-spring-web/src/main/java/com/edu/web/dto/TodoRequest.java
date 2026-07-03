package com.edu.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 할일 생성/수정 요청 DTO
 * record를 사용하여 불변 객체로 정의한다
 */
public record TodoRequest(
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 100, message = "제목은 100자 이하입니다")
    String title,

    String description,

    boolean completed
) {}
