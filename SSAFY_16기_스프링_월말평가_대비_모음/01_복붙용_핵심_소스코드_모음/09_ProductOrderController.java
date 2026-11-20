package com.ssafy.sample.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.sample.model.dto.OrderDto;
import com.ssafy.sample.model.dto.ProductDto;
import com.ssafy.sample.model.dto.SearchRequest;
import com.ssafy.sample.model.dto.SearchResponse;
import com.ssafy.sample.model.service.ProductOrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * [Controller = 웨이터 (프론트/Swagger 요청을 제일 먼저 받는 곳)]
 *
 * 클래스 위에 무조건 붙이는 어노테이션 3개:
 * 1. @RestController         : JSON 데이터나 문자열을 돌려주는 REST API 컨트롤러 선언
 * 2. @RequestMapping("/api") : 이 클래스 안의 모든 주소 앞에 공통으로 "/api" 를 붙임
 * 3. @RequiredArgsConstructor: 아래 'private final ProductOrderService service;' 자동 연결
 */
@Tag(name = "상품/주문 API", description = "상품 조회 및 주문 CRUD API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductOrderController {

    // ★ 컨트롤러는 서비스에게 일을 시킵니다. 반드시 private final!
    private final ProductOrderService service;

    // -------------------------------------------------------------------------
    // [패턴 1] 전체 목록 조회 -> GET /api/product
    // -------------------------------------------------------------------------
    @Operation(summary = "상품 목록 조회", description = "판매 중인 모든 상품의 리스트를 반환합니다.")
    @GetMapping("/product")
    public ResponseEntity<List<ProductDto>> getProductList() {
        List<ProductDto> list = service.getProductList();
        // ResponseEntity<>(보낼 데이터, HTTP 상태 코드) -> 200 OK 와 함께 리스트를 JSON으로 반환
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // -------------------------------------------------------------------------
    // [패턴 2] 단건 상세 조회 -> GET /api/order/{orderId}
    // ★ 주의: 주소에 {orderId} 처럼 중괄호가 있으면
    //         파라미터 앞에 반드시 @PathVariable("orderId") 를 붙이세요!
    //         (만약 주소가 /api/order?orderId=1 형태라면 아래 주석처럼 @RequestParam 사용)
    // -------------------------------------------------------------------------
    @Operation(summary = "주문 상세 조회", description = "주문 번호로 상세 내역을 조회합니다. ex) orderId=1")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetail(@PathVariable("orderId") int orderId) {
        OrderDto order = service.getOrderDetail(orderId);
        if (order != null) {
            return new ResponseEntity<>(order, HttpStatus.OK); // 찾았으면 200 OK + 주문정보
        } else {
            // 못 찾았으면 404 NOT_FOUND + 안내 메시지
            return new ResponseEntity<>("주문 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }

    /* (참고) 만약 문제에서 URL이 "/api/order?orderId=1" (쿼리 파라미터) 방식이면 아래처럼!
    @GetMapping("/order")
    public ResponseEntity<?> getOrderDetailByParam(@RequestParam("orderId") int orderId) {
        OrderDto order = service.getOrderDetail(orderId);
        if (order != null) {
            return new ResponseEntity<>(order, HttpStatus.OK);
        }
        return new ResponseEntity<>("주문 내역을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
    }
    */

    // -------------------------------------------------------------------------
    // [패턴 3] 일부 수정 / 상태 변경 -> PATCH /api/order/{orderId}/cancel
    // (문제에서 PUT을 요구하면 @PutMapping 으로만 바꾸면 됩니다!)
    // -------------------------------------------------------------------------
    @Operation(summary = "주문 취소", description = "주문의 상태를 CANCELLED로 변경합니다.")
    @PatchMapping("/order/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable("orderId") int orderId) {
        boolean isUpdated = service.cancelOrder(orderId);
        if (isUpdated) {
            return new ResponseEntity<>("주문이 성공적으로 취소되었습니다.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("잘못된 주문 번호이거나 취소할 수 없는 상태입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // -------------------------------------------------------------------------
    // [패턴 4] 데이터 등록 -> POST /api/order
    // ★ 주의 1: JSON 데이터를 받을 때는 반드시 파라미터 앞에 @RequestBody 를 붙이세요!
    // ★ 주의 2: 존재하지 않는 productId 등을 넣으면 DB 외래키 에러가 터지므로
    //           try-catch 로 감싸서 500 서버다운 대신 400 BAD_REQUEST를 돌려주는 것이 핵심!
    // -------------------------------------------------------------------------
    @Operation(summary = "주문 추가", description = "주문의 상태가 COMPLETED인 데이터를 넣습니다.")
    @PostMapping("/order")
    public ResponseEntity<String> insertOrder(@RequestBody OrderDto dto) {
        try {
            boolean isInserted = service.insertOrder(dto);
            if (isInserted) {
                return new ResponseEntity<>("주문이 성공적으로 추가되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("주문 추가에 실패했습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            // 없는 상품번호(productId)나 없는 회원(userId)으로 주문 넣었을 때 400 에러 응답
            return new ResponseEntity<>("존재하지 않는 상품번호 또는 잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // -------------------------------------------------------------------------
    // [패턴 5] 데이터 삭제 -> DELETE /api/order/{orderId}
    // -------------------------------------------------------------------------
    @Operation(summary = "주문 삭제", description = "주문 정보를 삭제합니다.")
    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable("orderId") int orderId) {
        try {
            boolean isDeleted = service.deleteOrder(orderId);
            if (isDeleted) {
                return new ResponseEntity<>("주문이 성공적으로 삭제되었습니다.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("잘못된 주문 번호이거나 삭제에 실패했습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("삭제 처리 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // -------------------------------------------------------------------------
    // [패턴 6] 동적 조건 검색 -> POST /api/order/search (JSON Body로 조건 전달 시)
    // ★ 주의: 강사님 샘플 정답 파일에는 실수로 @DeleteMapping 으로 되어 있지만,
    //         JSON Body(@RequestBody)로 검색 조건을 받을 때는 보통 @PostMapping 을 씁니다!
    //         (시험 문제지에서 요구하는 HTTP 메서드 GET/POST 를 꼭 확인하세요!)
    // -------------------------------------------------------------------------
    @Operation(summary = "상품 동적 조회", description = "상품 이름, 최소 가격, 최대 가격을 입력하여 동적 조회합니다.")
    @PostMapping("/order/search")
    public ResponseEntity<List<SearchResponse>> searchProduct(@RequestBody SearchRequest request) {
        List<SearchResponse> response = service.searchProduct(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /* (참고) 만약 시험에서 GET + 쿼리파라미터(?name=키보드&minPrice=10000) 로 검색하라고 하면?
       GET은 @RequestBody 를 못 쓰므로 @ModelAttribute 를 쓰면 됩니다!
    @GetMapping("/order/search")
    public ResponseEntity<List<SearchResponse>> searchProductGet(@ModelAttribute SearchRequest request) {
        return new ResponseEntity<>(service.searchProduct(request), HttpStatus.OK);
    }
    */
}
