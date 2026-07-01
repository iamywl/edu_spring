package com.edu.testing.controller;

import com.edu.testing.entity.Product;
import com.edu.testing.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 상품 REST 컨트롤러
 * - @WebMvcTest + MockMvc 슬라이스 테스트의 대상
 * - 비즈니스 로직은 ProductService에 위임하고, 컨트롤러는 HTTP 처리에만 집중
 * - 챕터의 단순함을 유지하기 위해 별도 DTO 없이 엔티티를 그대로 노출
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /** 전체 상품 조회: GET /api/products */
    @GetMapping
    public List<Product> getProducts() {
        return productService.findAll();
    }

    /** ID로 상품 조회: GET /api/products/{id} */
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.findById(id);
    }

    /** 상품 생성: POST /api/products (201 Created) */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product saved = productService.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * 존재하지 않는 상품 조회 시 404 응답 처리.
     * ProductService.findById()는 상품이 없으면 IllegalArgumentException을 던지므로,
     * 이를 HTTP 404(Not Found)로 변환한다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleNotFound(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
