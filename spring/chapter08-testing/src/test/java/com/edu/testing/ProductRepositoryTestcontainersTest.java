package com.edu.testing;

import com.edu.testing.entity.Product;
import com.edu.testing.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testcontainers를 활용한 Repository 통합 테스트
 * - Docker로 실제 PostgreSQL 컨테이너를 띄워서 테스트
 * - H2와 달리 실제 운영 환경과 동일한 DB에서 테스트 가능
 *
 * 사전 요구사항: Docker가 설치되어 있어야 합니다.
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("ProductRepository 통합 테스트 (Testcontainers)")
class ProductRepositoryTestcontainersTest {

    // Docker로 PostgreSQL 컨테이너를 자동 실행
    // static: 모든 테스트가 하나의 컨테이너를 공유 (성능 최적화)
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    // 동적으로 데이터소스 설정을 Testcontainers의 PostgreSQL로 변경
    // 컨테이너가 랜덤 포트로 시작되므로 동적 설정이 필요
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 데이터 초기화
        productRepository.deleteAll();
        productRepository.save(new Product("노트북", new BigDecimal("1500000"), 10));
        productRepository.save(new Product("마우스", new BigDecimal("50000"), 100));
        productRepository.save(new Product("키보드", new BigDecimal("80000"), 50));
        productRepository.save(new Product("노트북 파우치", new BigDecimal("30000"), 5));
    }

    @Test
    @DisplayName("PostgreSQL 컨테이너가 실행 중인지 확인")
    void containerIsRunning() {
        assertTrue(postgres.isRunning());
    }

    @Test
    @DisplayName("이름으로 검색")
    void findByNameContaining() {
        List<Product> result = productRepository.findByNameContaining("노트북");

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("가격 범위로 검색")
    void findByPriceBetween() {
        List<Product> result = productRepository.findByPriceBetween(
                new BigDecimal("40000"), new BigDecimal("100000")
        );

        // 마우스(50000), 키보드(80000) 2개가 조회되어야 함
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("재고 부족 상품 조회")
    void findByStockLessThan() {
        List<Product> result = productRepository.findByStockLessThan(10);

        // 노트북 파우치(재고 5)만 조회되어야 함
        assertEquals(1, result.size());
        assertEquals("노트북 파우치", result.get(0).getName());
    }

    @Test
    @DisplayName("상품 저장 및 조회")
    void saveAndFind() {
        Product saved = productRepository.save(
                new Product("모니터", new BigDecimal("500000"), 20)
        );

        // 저장 후 ID가 자동 생성되었는지 확인
        assertNotNull(saved.getId());

        // ID로 다시 조회
        Product found = productRepository.findById(saved.getId()).orElseThrow();
        assertEquals("모니터", found.getName());
        assertEquals(new BigDecimal("500000"), found.getPrice());
        assertEquals(20, found.getStock());
    }

    @Test
    @DisplayName("상품 삭제")
    void delete() {
        long countBefore = productRepository.count();
        Product product = productRepository.findByNameContaining("마우스").get(0);

        productRepository.delete(product);

        long countAfter = productRepository.count();
        assertEquals(countBefore - 1, countAfter);
    }

    @Test
    @DisplayName("전체 상품 수 확인")
    void count() {
        long count = productRepository.count();
        assertEquals(4, count);
    }
}
