package com.edu.testing.controller;

import com.edu.testing.entity.Product;
import com.edu.testing.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 상품 REST 컨트롤러
 * - @WebMvcTest + MockMvc 슬라이스 테스트의 대상
 * - 비즈니스 로직은 ProductService에 위임하고, 컨트롤러는 HTTP 처리에만 집중
 *
 * ※ 주의: 학습 단순화를 위해 엔티티를 직접 노출했지만, 실무에서는 DTO를 써야 한다
 *         (개념서 DTO 챕터 참고). 엔티티 직접 노출은 두 가지 위험이 있다.
 *   1) mass-assignment(과다 대입): 클라이언트가 id 등 서버가 관리해야 할 필드를 임의로 주입
 *      -> Product.id에 @JsonProperty(READ_ONLY)를 걸어 요청 역직렬화 시 무시하도록 방어했다.
 *   2) 과다 노출: 내부 필드가 그대로 응답에 노출됨 -> 실무에서는 응답 DTO로 필요한 필드만 노출한다.
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

    /**
     * 상품 생성: POST /api/products (201 Created)
     * - @Valid로 요청 본문의 Bean Validation(@NotBlank, @NotNull 등)을 수행한다.
     * - id는 Product.id의 @JsonProperty(READ_ONLY)로 인해 요청 본문 값이 무시되어 항상 null로 들어온다.
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
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
