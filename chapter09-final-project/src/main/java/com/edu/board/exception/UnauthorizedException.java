package com.edu.board.exception;

/**
 * 권한이 없는 작업을 시도할 때 발생하는 예외
 *
 * 예: 다른 사용자의 게시글을 수정하려고 할 때
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
