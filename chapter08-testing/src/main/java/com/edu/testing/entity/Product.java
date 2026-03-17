package com.edu.testing.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * 상품 엔티티
 * - 단위 테스트의 대상이 되는 도메인 객체
 * - 비즈니스 로직(재고 감소)을 포함
 */
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    // JPA 기본 생성자
    protected Product() {
    }

    public Product(String name, BigDecimal price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    /**
     * 재고를 감소시킨다.
     * 재고가 부족하면 IllegalStateException을 던진다.
     *
     * @param quantity 감소시킬 수량
     * @throws IllegalStateException 재고가 부족한 경우
     */
    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + this.stock);
        }
        this.stock -= quantity;
    }

    // Getter
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    // Setter
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
