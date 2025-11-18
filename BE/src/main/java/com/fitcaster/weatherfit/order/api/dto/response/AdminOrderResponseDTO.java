// AdminOrderResponseDTO.java
// 관리자 주문 내역 응답 DTO

package com.fitcaster.weatherfit.order.api.dto.response;

import com.fitcaster.weatherfit.order.domain.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
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
    private String imageURL;      // 상품 이미지 URL

    // Order 엔티티 목록으로부터 DTO 목록 생성
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
                                .imageURL(orderItem.getItem().getImageURL())
                                .build()))
                .collect(Collectors.toList());
    }

    // Order 엔티티로부터 DTO 생성 (Page 버전)
    public static List<AdminOrderResponseDTO> fromOrdersPage(Page<Order> orderPage) {
        return orderPage.getContent().stream()
                .flatMap(order -> order.getOrderItems().stream()
                        .map(orderItem -> AdminOrderResponseDTO.builder()
                                .orderId(order.getOrderNo())
                                .date(order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
                                .product(orderItem.getItem().getItemName())
                                .customer(order.getUser().getName())
                                .qty(orderItem.getQuantity())
                                .price(orderItem.getItem().getPrice() * orderItem.getQuantity())
                                .imageURL(orderItem.getItem().getImageURL())
                                .build()))
                .collect(Collectors.toList());
    }

    // 페이지 정보를 포함한 Wrapper DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagedResponse {
        private List<AdminOrderResponseDTO> orders;
        private int currentPage;
        private int totalPages;
        private long totalElements;
    }
}