package com.ssafy.sample.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [DTO = Data Transfer Object = 데이터를 담아서 나르는 도시락통]
 * - DB 테이블(products)의 컬럼(열)들과 1:1로 짝을 맞추는 클래스입니다.
 *
 * ★ 아래 Lombok 어노테이션 3개는 DTO 만들 때 무조건 세트처럼 붙이세요!
 *   @Data               -> getter, setter, toString 등을 자동으로 만들어 줌
 *   @NoArgsConstructor  -> 파라미터 없는 기본 생성자 (JSON 변환 시 필수!)
 *   @AllArgsConstructor -> 모든 필드를 받는 생성자
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    // DB 컬럼명: product_id -> 자바 변수명: productId (앞 단어 소문자 + 다음 단어 첫 글자 대문자)
    private int productId;

    // DB 컬럼명: name -> 자바 변수명: name
    private String name;

    // DB 컬럼명: price -> 자바 변수명: price
    private int price;

    // DB 컬럼명: stock -> 자바 변수명: stock
    private int stock;

    // (선택 확장 필드) 상품 목록이나 검색 결과에 최근 구매자 이름을 같이 담아야 할 때 사용
    private String lastCustomerName;
}
