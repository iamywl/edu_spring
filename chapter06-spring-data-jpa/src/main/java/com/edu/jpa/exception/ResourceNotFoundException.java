package com.edu.jpa.exception;

/**
 * 요청한 리소스(회원, 팀 등)를 찾을 수 없을 때 발생시키는 예외
 *
 * <p>이 예외는 전역 예외 처리기에서 HTTP 404(Not Found)로 변환된다.
 * "존재하지 않는 데이터를 조회/수정/삭제하려 한 경우"의 의미를 타입으로 명확히 표현한다.
 *
 * <p>예전 코드처럼 모든 상황에 {@code IllegalArgumentException}을 던지면
 * "없는 회원 조회(404여야 함)"와 "잘못된 입력(400)"을 구분할 수 없어
 * 클라이언트가 적절히 대응할 수 없다. 예외 타입을 분리하는 이유다.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
