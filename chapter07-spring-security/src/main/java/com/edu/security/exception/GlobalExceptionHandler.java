package com.edu.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 전역 예외 처리기 (@RestControllerAdvice)
 *
 * 컨트롤러/서비스 계층 "내부"에서 던져지는 예외를 HTTP 응답으로 변환한다.
 *
 * 주의: 보안 "필터" 계층(토큰 없음/유효하지 않음 → 401, 권한 부족 → 403)은
 * SecurityConfig의 AuthenticationEntryPoint / AccessDeniedHandler가 이미 처리한다.
 * 이 핸들러는 그 이전 필터를 통과한 뒤 컨트롤러/서비스에서 발생하는 예외
 * (예: 중복 회원가입, 로그인 자격 증명 오류)를 담당한다.
 *
 * 응답 본문은 SecurityConfig의 에러 포맷과 동일하게 {status, error, message}로 맞춘다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 잘못된 요청 인자 → 409 Conflict
     *
     * AuthService.register()는 사용자명이 이미 존재하면 IllegalArgumentException을 던진다.
     * 중복 회원가입은 "이미 존재하는 자원과의 충돌"이므로 409 Conflict가 의미상 적절하다.
     * (Spring 기본 처리에 맡기면 500 Internal Server Error가 나가므로 반드시 매핑한다.)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    /**
     * 로그인 자격 증명 오류 → 401 Unauthorized
     *
     * AuthService.login()에서 AuthenticationManager가 비밀번호 불일치 시 BadCredentialsException을,
     * 사용자 조회 실패 시 UsernameNotFoundException을 던진다.
     * 둘 다 "인증되지 않음"이므로 401로 통일한다.
     * (보안상 존재하지 않는 사용자와 비밀번호 오류를 구분해 노출하지 않는 편이 안전하다.)
     */
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleAuthenticationFailure(RuntimeException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    /**
     * 입력값 검증 실패 → 400 Bad Request
     *
     * @Valid 검증 실패(예: 비밀번호 8자 미만) 시 MethodArgumentNotValidException이 발생한다.
     * 첫 번째 필드 오류 메시지를 응답에 담아 클라이언트가 원인을 알 수 있게 한다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "요청 값이 유효하지 않습니다.";
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * SecurityConfig의 writeError()와 동일한 {status, error, message} 형식으로 응답을 만든다.
     */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
