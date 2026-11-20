package com.ssafy.sample.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ssafy.sample.model.dto.OrderDto;
import com.ssafy.sample.model.dto.ProductDto;
import com.ssafy.sample.model.dto.SearchResponse;

/**
 * [DAO = 창고지기 인터페이스]
 * - 자바(Service)와 SQL(Mapper.xml)을 연결해 주는 다리입니다.
 * - 구현 클래스는 직접 만들지 않습니다! @Mapper를 붙이면 MyBatis가 알아서 만들어 줍니다.
 *
 * ★★★ 절대 규칙 2가지 ★★★
 * 1. 이 인터페이스의 메서드 이름(예: getProductList)과
 *    Mapper.xml 파일 안의 <select id="getProductList"> 의 id 값이 글자 하나 안 틀리고 똑같아야 합니다!
 * 2. INSERT, UPDATE, DELETE의 반환 타입을 boolean 으로 쓰면
 *    MyBatis가 자동으로 (변경된 행 수 > 0) 이면 true, 0이면 false를 돌려줍니다!
 */
@Mapper
public interface ProductOrderDao {

    // 1. 전체 상품 목록 조회 -> 여러 개니까 List<DTO>
    List<ProductDto> getProductList();

    // 2. 주문 상세 조회 (1건) -> 결과 1개니까 OrderDto (없으면 null 반환)
    // @Param("orderId") : XML 안의 #{orderId} 이름과 연결해 줌
    OrderDto getOrderDetail(@Param("orderId") int orderId);

    // 3. 주문 취소 (UPDATE) -> 성공하면 true, 실패(없는 번호)하면 false
    boolean cancelOrder(@Param("orderId") int orderId);

    // 4. 주문 추가 (INSERT) -> DTO 객체 하나만 넘길 때는 @Param 없이 XML에서 #{productId}, #{userId} 바로 사용 가능
    boolean insertOrder(OrderDto order);

    // 5. 주문 삭제 (DELETE) -> 성공하면 true, 실패하면 false
    boolean deleteOrder(@Param("orderId") int orderId);

    // 6. 동적 조건 검색
    // ★★★ 중요: 파라미터가 2개 이상일 때는 반드시 앞에 @Param("이름") 을 붙여주세요!
    // (안 붙이면 Parameter 'name' not found 에러가 날 수 있습니다)
    List<SearchResponse> searchProduct(
            @Param("name") String name,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice
    );

    // =========================================================================
    // [심화 대비] 만약 주문 시 재고/포인트 차감, 취소 시 재고 복구 문제가 나온다면 아래도 추가!
    // =========================================================================
    ProductDto getProductById(@Param("productId") int productId);
    boolean decreaseStock(@Param("productId") int productId, @Param("quantity") int quantity);
    boolean increaseStock(@Param("productId") int productId, @Param("quantity") int quantity);
    boolean decreasePoint(@Param("userId") String userId, @Param("amount") int amount);
}
