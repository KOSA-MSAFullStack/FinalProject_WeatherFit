package com.fitcaster.weatherfit.order.api.dto.response;

import com.fitcaster.weatherfit.order.domain.entity.Order;
import com.fitcaster.weatherfit.order.domain.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * author: 이상우
 * 주문 내역 목록 조회 시 사용되는 응답 DTO
 */
@Getter
@Builder
public class OrderHistoryItemResponse {
    // 주문 정보
    private Long orderId;           // 주문 ID
    private String orderNo;         // 주문 번호 (UUID)
    private LocalDateTime orderDate; // 주문일자

    // 개별 상품 정보 (OrderItem 및 Item 엔티티에서 추출)
    private Long orderItemId;       // 주문 항목 ID (리뷰 작성 시 필요할 수 있음)
    private Long itemId;            // 상품 ID (상세보기 이동 시 필요)
    private String itemName;        // 상품 이름
    private int quantity;           // 주문 수량
    private Integer itemPrice;      // 상품 단가 (구매 당시 가격)
    private String itemImage;       // 상품 이미지 URL

    /**
     * OrderItem 엔티티와 그 부모인 Order 엔티티를 기반으로 DTO를 생성하는 정적 팩토리 메서드
     * @param orderItem 개별 주문 항목 엔티티
     * @return OrderHistoryItemResponse DTO
     */
    public static OrderHistoryItemResponse from(OrderItem orderItem) {

        return OrderHistoryItemResponse.builder()
                // 주문 정보
                .orderId(orderItem.getOrder().getId())
                .orderNo(orderItem.getOrder().getOrderNo())
                .orderDate(orderItem.getOrder().getOrderDate())

                // 상품 정보
                .orderItemId(orderItem.getId())
                .itemId(orderItem.getItem().getItemId())
                .itemName(orderItem.getItem().getItemName())
                .quantity(orderItem.getQuantity())
                .itemPrice(orderItem.getItem().getPrice())
                .itemImage(orderItem.getItem().getImageURL())
                .build();
    }
}