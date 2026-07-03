package com.edu.jpa.exception;

import com.edu.jpa.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 전역 예외 처리기 (@RestControllerAdvice)
 *
 * <p>모든 컨트롤러에서 발생하는 예외를 한 곳에서 처리하여
 * <b>일관된 에러 응답 형식</b>과 <b>정확한 HTTP 상태 코드</b>를 보장한다.
 *
 * <p>예외 타입별로 올바른 상태 코드를 매핑하는 것이 핵심이다.
 * <ul>
 *   <li>리소스 없음 → 404 Not Found</li>
 *   <li>중복(상태 충돌) → 409 Conflict</li>
 *   <li>입력 검증 실패 → 400 Bad Request</li>
 *   <li>그 외 예상치 못한 오류 → 500 Internal Server Error</li>
 * </ul>
 *
 * <p>(Chapter 05의 GlobalExceptionHandler와 동일한 패턴을 따른다.
 * 컨트롤러마다 흩어진 {@code @ExceptionHandler}를 두지 않고 한 곳에 모은다.)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 리소스를 찾을 수 없을 때 → 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        log.warn("리소스를 찾을 수 없음: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }

    /**
     * 중복 데이터(이메일, 팀 이름 등) → 409 Conflict
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex) {
        log.warn("중복 데이터: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("DUPLICATE", ex.getMessage()));
    }

    /**
     * @Valid 검증 실패 → 400 Bad Request
     * 필드별 에러 메시지를 "필드명: 메시지" 형식으로 합친다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("유효성 검증 실패: {}", message);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", message));
    }

    /**
     * 그 외 예상치 못한 예외 → 500 Internal Server Error
     * 상세 내용은 로그에만 남기고, 클라이언트에는 일반 메시지를 반환한다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("예상치 못한 서버 오류 발생", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다"));
    }
}
