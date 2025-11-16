package com.fitcaster.weatherfit.order.api.dto.response;

import com.fitcaster.weatherfit.order.domain.entity.Order;
import com.fitcaster.weatherfit.order.domain.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author: 이상우
 * 주문 내역 목록 조회 시 사용되는 응답 DTO
 */
@Getter
@Builder // @Builder는 그대로 둡니다.
public class OrderHistoryResponse {
    private Long orderId;
    private String orderNo;
    private LocalDateTime orderDate;
    private List<OrderHistoryItemDto> orderItems;

    public static OrderHistoryResponse from(Order order) {
        return OrderHistoryResponse.builder()
                .orderId(order.getId())
                .orderNo(order.getOrderNo())
                .orderDate(order.getOrderDate())
                .orderItems(order.getOrderItems().stream()
                        .map(OrderHistoryItemDto::from)
                        .collect(Collectors.toList()))
                .build();
    }
}