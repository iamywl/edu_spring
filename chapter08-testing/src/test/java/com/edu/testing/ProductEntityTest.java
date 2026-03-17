package com.edu.testing;

import com.edu.testing.entity.Product;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 기본 테스트 예제
 * - 단위 테스트: 외부 의존성 없이 Product 엔티티만 테스트
 * - Assertions, Lifecycle, Parameterized Test, Nested Test 활용
 */
@DisplayName("Product 엔티티 단위 테스트")
class ProductEntityTest {

    private Product product;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 새로운 Product 객체를 생성
        product = new Product("테스트상품", new BigDecimal("10000"), 100);
    }

    @Test
    @DisplayName("상품 생성 테스트")
    void createProduct() {
        // assertAll: 여러 단언을 그룹화. 하나가 실패해도 나머지를 계속 실행
        assertAll(
                () -> assertEquals("테스트상품", product.getName()),
                () -> assertEquals(new BigDecimal("10000"), product.getPrice()),
                () -> assertEquals(100, product.getStock())
        );
    }

    @Test
    @DisplayName("재고 감소 - 정상")
    void decreaseStock_success() {
        product.decreaseStock(10);
        assertEquals(90, product.getStock());
    }

    @Test
    @DisplayName("재고 감소 - 재고 부족 시 예외")
    void decreaseStock_insufficientStock() {
        // assertThrows: 특정 예외가 발생하는지 검증
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> product.decreaseStock(101)
        );
        // 예외 메시지도 검증 가능
        assertTrue(exception.getMessage().contains("재고가 부족합니다"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 50, 100})
    @DisplayName("다양한 수량으로 재고 감소 테스트")
    void decreaseStock_variousQuantities(int quantity) {
        // 각 수량에 대해 예외가 발생하지 않는지 검증
        assertDoesNotThrow(() -> product.decreaseStock(quantity));
    }

    @ParameterizedTest
    @CsvSource({"'상품A', 1000, 10", "'상품B', 2000, 20", "'상품C', 3000, 30"})
    @DisplayName("다양한 상품 생성 파라미터 테스트")
    void createProduct_parameterized(String name, int price, int stock) {
        Product p = new Product(name, new BigDecimal(price), stock);
        assertNotNull(p);
        assertEquals(name, p.getName());
    }

    @Nested
    @DisplayName("재고 관련 테스트 그룹")
    class StockTests {

        @Test
        @DisplayName("재고를 0으로 감소")
        void decreaseToZero() {
            product.decreaseStock(100);
            assertEquals(0, product.getStock());
        }

        @Test
        @DisplayName("0개 감소는 성공")
        void decreaseZero() {
            product.decreaseStock(0);
            assertEquals(100, product.getStock());
        }
    }

    @Nested
    @DisplayName("Setter 테스트 그룹")
    class SetterTests {

        @Test
        @DisplayName("이름 변경")
        void setName() {
            product.setName("변경된상품");
            assertEquals("변경된상품", product.getName());
        }

        @Test
        @DisplayName("가격 변경")
        void setPrice() {
            product.setPrice(new BigDecimal("20000"));
            assertEquals(new BigDecimal("20000"), product.getPrice());
        }

        @Test
        @DisplayName("재고 변경")
        void setStock() {
            product.setStock(200);
            assertEquals(200, product.getStock());
        }
    }
}
