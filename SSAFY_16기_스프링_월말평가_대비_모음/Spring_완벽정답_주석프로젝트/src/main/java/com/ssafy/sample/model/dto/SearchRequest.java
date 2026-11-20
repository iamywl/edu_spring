package com.ssafy.sample.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [동적 검색 요청 DTO] (클라이언트가 보내는 검색 조건을 담는 바구니)
 * ★ 스켈레톤 코드에는 없을 수 있으므로, 검색 문제가 나오면 model.dto 패키지에 직접 새 클래스로 만드세요!
 *
 * ★★★ 초중요 주의사항 ★★★
 * 숫자 검색 조건(minPrice, maxPrice)은 절대 int로 쓰지 말고 반드시 Integer 로 쓰세요!
 * - 이유: 사용자가 가격을 입력 안 하면 null이 들어와야 XML의 <if test="minPrice != null"> 이 동작합니다.
 * - 만약 int로 쓰면 입력 안 했을 때 자동으로 0이 들어가서 "가격 <= 0" 조건이 걸려 결과가 0개 나옵니다!
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {
    private String name;      // 검색할 상품명 (일부만 입력해도 LIKE 검색)
    private Integer minPrice; // 최소 가격 (int X -> Integer O)
    private Integer maxPrice; // 최대 가격 (int X -> Integer O)
}
