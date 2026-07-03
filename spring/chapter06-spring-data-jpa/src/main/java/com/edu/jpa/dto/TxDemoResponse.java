package com.edu.jpa.dto;

import java.util.List;

/**
 * 트랜잭션 데모 응답 DTO (record)
 *
 * <p>각 tx-demo 엔드포인트는 "무슨 일이 일어났는지"를 학습자가 응답만 보고도
 * 해석할 수 있도록, 시나리오 이름 · 결과 요약 · 단계별 설명 · 실행 전후의
 * 회원 수를 함께 돌려준다.
 *
 * <p>회원 수의 변화(before → after)가 곧 "커밋되었는가 / 롤백되었는가"의 증거다.
 *
 * @param scenario          시나리오 이름
 * @param explanation       무슨 일이 일어났는지 한 줄 요약
 * @param details           단계별 설명 (읽는 순서대로)
 * @param memberCountBefore 시나리오 실행 전 회원 수
 * @param memberCountAfter  시나리오 실행 후 회원 수 (before와 비교해 커밋/롤백 판단)
 */
public record TxDemoResponse(
        String scenario,
        String explanation,
        List<String> details,
        long memberCountBefore,
        long memberCountAfter
) {
}
