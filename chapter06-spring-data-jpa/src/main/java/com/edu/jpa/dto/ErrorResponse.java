package com.edu.jpa.dto;

/**
 * 공통 에러 응답 DTO (record)
 *
 * <p>모든 에러 응답을 동일한 형태(JSON)로 통일하기 위한 DTO다.
 * 예: {@code {"code": "NOT_FOUND", "message": "존재하지 않는 회원입니다: 99"}}
 *
 * @param code    에러 종류를 식별하는 코드 (예: NOT_FOUND, DUPLICATE, VALIDATION_ERROR)
 * @param message 사람이 읽을 수 있는 에러 메시지
 */
public record ErrorResponse(
        String code,
        String message
) {
}
