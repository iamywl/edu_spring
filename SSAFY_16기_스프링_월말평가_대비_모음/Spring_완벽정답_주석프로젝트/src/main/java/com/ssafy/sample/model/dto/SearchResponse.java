package com.ssafy.sample.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [동적 검색 응답 DTO] (검색 결과를 클라이언트에게 돌려줄 때 담는 바구니)
 * ★ DB 조회 결과를 바로 담으려면 application.properties에
 *   mybatis.configuration.map-underscore-to-camel-case=true 가 반드시 있어야 합니다!
 *   (그래야 SQL의 product_id -> productId, last_customer_name -> lastCustomerName 으로 자동 저장됨)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    private int productId;           // SQL: p.product_id
    private String name;             // SQL: p.name
    private int price;               // SQL: p.price
    private int stock;               // SQL: p.stock
    private String lastCustomerName; // SQL: 서브쿼리 결과 AS last_customer_name
}
