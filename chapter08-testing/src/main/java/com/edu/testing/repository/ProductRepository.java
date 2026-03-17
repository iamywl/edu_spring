package com.edu.testing.repository;

import com.edu.testing.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 상품 리포지토리
 * - Spring Data JPA가 구현체를 자동 생성
 * - 메서드 이름 기반 쿼리 자동 생성
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** 이름에 특정 문자열이 포함된 상품 검색 */
    List<Product> findByNameContaining(String name);

    /** 가격 범위로 상품 검색 */
    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

    /** 재고가 특정 수량 미만인 상품 검색 */
    List<Product> findByStockLessThan(Integer stock);
}
