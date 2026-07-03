package com.edu.web.dto;

import java.time.LocalDateTime;

/**
 * 표준화된 에러 응답 DTO
 * 모든 에러 응답을 일관된 형식으로 반환하기 위해 사용한다
 */
public record ErrorResponse(
    String code,
    String message,
    LocalDateTime timestamp
) {
    /**
     * 간편 생성자 - 타임스탬프를 자동으로 현재 시각으로 설정한다
     */
    public ErrorResponse(String code, String message) {
        this(code, message, LocalDateTime.now());
    }
}
