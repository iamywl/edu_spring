package com.edu.testing.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * 상품 엔티티
 * - 단위 테스트의 대상이 되는 도메인 객체
 * - 비즈니스 로직(재고 감소)을 포함
 */
@Entity
@Table(name = "product")
public class Product {

    // id는 DB가 채번(IDENTITY)하는 값이므로 클라이언트가 요청 본문으로 설정할 수 없어야 한다.
    // READ_ONLY: JSON 응답에는 포함되지만, 요청 역직렬화 시 무시된다.
    // -> 클라이언트가 임의의 id를 심어 다른 레코드를 덮어쓰는 mass-assignment(과다 대입)를 막는다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "상품명은 필수입니다")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "가격은 필수입니다")
    @PositiveOrZero(message = "가격은 0 이상이어야 합니다")
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull(message = "재고는 필수입니다")
    @PositiveOrZero(message = "재고는 0 이상이어야 합니다")
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
