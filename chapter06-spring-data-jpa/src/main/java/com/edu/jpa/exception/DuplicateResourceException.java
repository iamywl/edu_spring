package com.edu.jpa.exception;

/**
 * 이미 존재하는 데이터를 중복 생성하려 할 때 발생시키는 예외
 * (예: 이미 사용 중인 이메일, 이미 존재하는 팀 이름)
 *
 * <p>전역 예외 처리기에서 HTTP 409(Conflict)로 변환된다.
 * "현재 리소스 상태와 충돌한다"는 의미이므로 400(Bad Request)보다 409가 정확하다.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
