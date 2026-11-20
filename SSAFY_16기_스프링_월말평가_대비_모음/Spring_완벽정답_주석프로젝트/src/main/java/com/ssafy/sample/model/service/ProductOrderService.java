package com.ssafy.sample.model.service;

import java.util.List;

import com.ssafy.sample.model.dto.OrderDto;
import com.ssafy.sample.model.dto.ProductDto;
import com.ssafy.sample.model.dto.SearchRequest;
import com.ssafy.sample.model.dto.SearchResponse;

/**
 * [Service 인터페이스 = 메뉴판]
 * - 컨트롤러가 호출할 기능 목록(메서드 선언)만 적어두는 곳입니다.
 * - 실제 코드는 이 인터페이스를 구현(implements)한 ProductOrderServiceImpl 클래스에 작성합니다.
 */
public interface ProductOrderService {

    List<ProductDto> getProductList();

    OrderDto getOrderDetail(int orderId);

    boolean cancelOrder(int orderId);

    boolean insertOrder(OrderDto order);

    boolean deleteOrder(int orderId);

    List<SearchResponse> searchProduct(SearchRequest request);
}
