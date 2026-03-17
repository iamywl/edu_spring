package com.edu.web.exception;

/**
 * 할일을 찾을 수 없을 때 발생하는 커스텀 예외
 * RuntimeException을 상속하여 체크 예외 처리를 생략할 수 있다
 */
public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(Long id) {
        super("할일을 찾을 수 없습니다. ID: " + id);
    }
}
