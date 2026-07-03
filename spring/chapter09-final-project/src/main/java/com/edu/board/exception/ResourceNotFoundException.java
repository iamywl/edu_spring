package com.edu.board.exception;

/**
 * 요청한 리소스를 찾을 수 없을 때 발생하는 예외
 *
 * 예: 존재하지 않는 게시글 ID로 조회 시
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + "을(를) 찾을 수 없습니다. (ID: " + id + ")");
    }
}
