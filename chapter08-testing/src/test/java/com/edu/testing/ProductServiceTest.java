package com.edu.testing;

import com.edu.testing.entity.Product;
import com.edu.testing.repository.ProductRepository;
import com.edu.testing.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Mockito를 활용한 서비스 단위 테스트
 * - ProductRepository를 Mock으로 대체하여 격리된 테스트
 * - BDD 스타일 (Given-When-Then) 패턴 사용
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 단위 테스트 (Mockito)")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("전체 상품 조회")
    void findAll() {
        // given - 테스트 데이터 준비 및 Mock 행동 정의
        List<Product> products = List.of(
                new Product("상품1", new BigDecimal("1000"), 10),
                new Product("상품2", new BigDecimal("2000"), 20)
        );
        given(productRepository.findAll()).willReturn(products);

        // when - 테스트 대상 실행
        List<Product> result = productService.findAll();

        // then - 결과 검증
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("ID로 상품 조회 - 존재하는 경우")
    void findById_found() {
        // given
        Product product = new Product("테스트", new BigDecimal("1000"), 10);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // when
        Product result = productService.findById(1L);

        // then
        assertEquals("테스트", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("ID로 상품 조회 - 존재하지 않는 경우")
    void findById_notFound() {
        // given
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.findById(999L)
        );
        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("상품 저장")
    void save() {
        // given
        Product product = new Product("새상품", new BigDecimal("5000"), 50);
        given(productRepository.save(any(Product.class))).willReturn(product);

        // when
        Product result = productService.save(product);

        // then
        assertEquals("새상품", result.getName());
        assertEquals(new BigDecimal("5000"), result.getPrice());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("상품 구매 - 정상")
    void purchaseProduct_success() {
        // given
        Product product = new Product("테스트상품", new BigDecimal("10000"), 100);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // when
        productService.purchaseProduct(1L, 10);

        // then - 재고가 감소되었는지 검증
        assertEquals(90, product.getStock());
    }

    @Test
    @DisplayName("상품 구매 - 재고 부족")
    void purchaseProduct_insufficientStock() {
        // given
        Product product = new Product("테스트상품", new BigDecimal("10000"), 5);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // when & then
        assertThrows(
                IllegalStateException.class,
                () -> productService.purchaseProduct(1L, 10)
        );
    }

    @Test
    @DisplayName("재고 부족 상품 조회")
    void findLowStockProducts() {
        // given
        List<Product> lowStockProducts = List.of(
                new Product("상품A", new BigDecimal("1000"), 3)
        );
        given(productRepository.findByStockLessThan(5)).willReturn(lowStockProducts);

        // when
        List<Product> result = productService.findLowStockProducts(5);

        // then
        assertEquals(1, result.size());
        assertEquals("상품A", result.get(0).getName());
        verify(productRepository).findByStockLessThan(5);
    }
}
