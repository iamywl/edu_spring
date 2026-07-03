package com.edu.testing;

import com.edu.testing.controller.ProductController;
import com.edu.testing.entity.Product;
import com.edu.testing.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @WebMvcTest를 활용한 Controller 슬라이스 테스트
 * - Controller 레이어와 MVC 관련 빈만 로드하여 빠르게 API를 검증
 * - ProductService는 실제 빈 대신 @MockitoBean으로 Mock 처리
 * - MockMvc로 실제 서버 없이 HTTP 요청/응답을 시뮬레이션
 *
 * 참고: Spring Boot 3.4부터 @MockBean이 deprecated 되었고,
 *       org.springframework.test.context.bean.override.mockito.MockitoBean을 사용한다.
 */
@WebMvcTest(ProductController.class)
@DisplayName("ProductController 슬라이스 테스트 (@WebMvcTest + MockMvc)")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Spring Boot 3.4+ : @MockBean 대신 @MockitoBean 사용
    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("상품 목록 조회 - 200 OK와 JSON 배열 반환")
    void getProducts() throws Exception {
        // given - Service가 두 개의 상품을 반환하도록 정의
        List<Product> products = List.of(
                new Product("상품1", new BigDecimal("1000"), 10),
                new Product("상품2", new BigDecimal("2000"), 20)
        );
        given(productService.findAll()).willReturn(products);

        // when & then - GET 요청 시 200과 길이 2의 배열이 반환되는지 검증
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("상품1"))
                .andExpect(jsonPath("$[1].name").value("상품2"));
    }

    @Test
    @DisplayName("ID로 상품 조회 - 존재하는 경우 200과 상품 정보 반환")
    void getProduct_found() throws Exception {
        // given
        Product product = new Product("노트북", new BigDecimal("1500000"), 5);
        given(productService.findById(1L)).willReturn(product);

        // when & then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("노트북"))
                .andExpect(jsonPath("$.price").value(1500000))
                .andExpect(jsonPath("$.stock").value(5));
    }

    @Test
    @DisplayName("ID로 상품 조회 - 존재하지 않으면 404 Not Found")
    void getProduct_notFound() throws Exception {
        // given - Service가 IllegalArgumentException을 던지도록 정의
        given(productService.findById(999L))
                .willThrow(new IllegalArgumentException("상품을 찾을 수 없습니다: 999"));

        // when & then - ExceptionHandler가 404로 변환하는지 검증
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("상품 생성 - 201 Created와 생성된 상품 반환")
    void createProduct() throws Exception {
        // given
        Product newProduct = new Product("키보드", new BigDecimal("80000"), 50);
        given(productService.save(any(Product.class))).willReturn(newProduct);

        // when & then - JSON 본문으로 POST 요청 시 201과 저장된 상품이 반환되는지 검증
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("키보드"))
                .andExpect(jsonPath("$.price").value(80000))
                .andExpect(jsonPath("$.stock").value(50));
    }
}
