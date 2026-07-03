package com.edu.testing.service;

import com.edu.testing.entity.Product;
import com.edu.testing.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 상품 서비스
 * - Mockito 테스트의 대상
 * - Repository를 의존하므로 단위 테스트 시 Mock으로 대체
 */
@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /** 전체 상품 조회 */
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /** ID로 상품 조회 (없으면 예외) */
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + id));
    }

    /** 상품 저장 */
    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    /** 상품 구매 (재고 감소) */
    @Transactional
    public void purchaseProduct(Long productId, int quantity) {
        Product product = findById(productId);
        product.decreaseStock(quantity);
    }

    /** 재고 부족 상품 조회 */
    public List<Product> findLowStockProducts(int threshold) {
        return productRepository.findByStockLessThan(threshold);
    }
}
