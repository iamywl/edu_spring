package com.ssafy.sample.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [주문 정보 DTO]
 * - orders 테이블의 컬럼들과 1:1 매칭됩니다.
 * - ★ 중요: @Data, @NoArgsConstructor, @AllArgsConstructor 없으면
 *   JSON 데이터(@RequestBody)를 받을 때 400 Bad Request 에러가 날 수 있습니다!
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private int orderId;      // DB: order_id (AUTO_INCREMENT 자동 증가)
    private int productId;    // DB: product_id (외래키: products 테이블 참조)
    private String userId;    // DB: user_id (외래키: users 테이블 참조)
    private String orderDate; // DB: order_date (DATETIME을 문자열로 간편 조회)
    private int quantity;     // DB: quantity (주문 수량)
    private String status;    // DB: status ('COMPLETED' 또는 'CANCELLED')

    // ★ SQL에서 products 테이블과 JOIN해서 상품명(p.name AS product_name)을 같이 가져올 때 사용!
    private String productName;
}
