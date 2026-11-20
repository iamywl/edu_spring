package com.ssafy.sample.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * [전역 예외 처리기]
 * - 컨트롤러마다 try-catch 를 일일이 쓰기 귀찮을 때 한 곳에서 예외를 잡아서
 *   원하는 상태 코드(400, 500 등)와 메시지를 깔끔하게 돌려주는 클래스입니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 잘못된 입력값(IllegalArgumentException / IllegalStateException) -> 400 Bad Request
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 그 외 예상치 못한 모든 예외 -> 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllException(Exception e) {
        return new ResponseEntity<>("서버 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
