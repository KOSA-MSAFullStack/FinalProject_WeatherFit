// AdminOrderResponseDTO.java
// 관리자 주문 내역 응답 DTO

package com.fitcaster.weatherfit.order.api.dto.response;

import com.fitcaster.weatherfit.order.domain.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

// * author: 김기성
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderResponseDTO {
    private String orderId;      // 주문번호 (orderNo)
    private String date;          // 주문일시
    private String product;       // 상품명
    private String customer;      // 고객명
    private int qty;              // 수량
    private int price;            // 판매금액

    // Order 엔티티로부터 DTO 생성
    public static List<AdminOrderResponseDTO> fromOrders(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.getOrderItems().stream()
                        .map(orderItem -> AdminOrderResponseDTO.builder()
                                .orderId(order.getOrderNo())
                                .date(order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
                                .product(orderItem.getItem().getItemName())
                                .customer(order.getUser().getName())
                                .qty(orderItem.getQuantity())
                                .price(orderItem.getItem().getPrice() * orderItem.getQuantity())
                                .build()))
                .collect(Collectors.toList());
    }
}