package com.ssafy.sample.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.sample.model.dao.ProductOrderDao;
import com.ssafy.sample.model.dto.OrderDto;
import com.ssafy.sample.model.dto.ProductDto;
import com.ssafy.sample.model.dto.SearchRequest;
import com.ssafy.sample.model.dto.SearchResponse;

import lombok.RequiredArgsConstructor;

/**
 * [ServiceImpl = 주방장 (실제 일 처리 클래스)]
 * ★★★ 스켈레톤(Spring_sample.zip)에는 이 파일이 아예 없습니다! ★★★
 * -> model.service 패키지에 우클릭 -> New -> Class -> 이름: ProductOrderServiceImpl 로 직접 만드세요!
 *
 * 필수 어노테이션 2개:
 * 1. @Service             : 스프링에게 "얘가 서비스 부품(Bean)이야"라고 알려줌 (없으면 서버 실행 시 에러!)
 * 2. @RequiredArgsConstructor : 아래 'private final ProductOrderDao dao;' 에 자동으로 DAO를 꽂아줌(생성자 주입)
 */
@Service
@RequiredArgsConstructor
public class ProductOrderServiceImpl implements ProductOrderService {

    // ★ 반드시 'private final'로 선언해야 @RequiredArgsConstructor 가 주입해 줍니다!
    private final ProductOrderDao dao;

    @Override
    public List<ProductDto> getProductList() {
        return dao.getProductList();
    }

    @Override
    public OrderDto getOrderDetail(int orderId) {
        return dao.getOrderDetail(orderId);
    }

    @Override
    public boolean cancelOrder(int orderId) {
        return dao.cancelOrder(orderId);
    }

    @Override
    public boolean insertOrder(OrderDto order) {
        return dao.insertOrder(order);
    }

    @Override
    public boolean deleteOrder(int orderId) {
        return dao.deleteOrder(orderId);
    }

    @Override
    public List<SearchResponse> searchProduct(SearchRequest request) {
        // SearchRequest 바구니에서 3개 값을 꺼내서 DAO에게 전달
        return dao.searchProduct(
                request.getName(),
                request.getMinPrice(),
                request.getMaxPrice()
        );
    }

    /* =========================================================================
     * [심화 응용 대비] 만약 시험에서
     * "주문 시 재고(stock)를 확인하고 차감하라 / 취소 시 재고를 복구하라"
     * 는 요구사항이 나온다면 위 cancelOrder, insertOrder 대신 아래 코드를 사용하세요!
     *
     * @Transactional(rollbackFor = Exception.class)
     * -> 중간에 하나라도 실패해서 예외가 터지면 DB 변경을 전부 없던 일로 되돌려(롤백) 줍니다!
     * =========================================================================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertOrder(OrderDto order) {
        if (order.getQuantity() <= 0) {
            throw new IllegalArgumentException("주문 수량은 1개 이상이어야 합니다.");
        }
        ProductDto product = dao.getProductById(order.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }
        // 1) 재고 차감 (재고 부족 시 false 반환 -> 예외 발생 -> 자동 롤백)
        boolean stockDecreased = dao.decreaseStock(order.getProductId(), order.getQuantity());
        if (!stockDecreased) {
            throw new IllegalStateException("상품 재고가 부족합니다.");
        }
        // 2) (포인트 차감 조건도 있다면 아래 주석 해제)
        // int totalPrice = product.getPrice() * order.getQuantity();
        // if (!dao.decreasePoint(order.getUserId(), totalPrice)) {
        //     throw new IllegalStateException("보유 포인트가 부족합니다.");
        // }
        // 3) 주문 저장
        return dao.insertOrder(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(int orderId) {
        OrderDto order = dao.getOrderDetail(orderId);
        if (order == null || "CANCELLED".equals(order.getStatus())) {
            return false; // 없는 주문이거나 이미 취소된 주문
        }
        boolean cancelled = dao.cancelOrder(orderId);
        if (cancelled) {
            dao.increaseStock(order.getProductId(), order.getQuantity()); // 재고 복구
        }
        return cancelled;
    }
    */
}
