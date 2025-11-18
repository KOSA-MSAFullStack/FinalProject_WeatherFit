package com.fitcaster.weatherfit.order.api.dto.response;

import com.fitcaster.weatherfit.order.domain.entity.OrderItem;
import com.fitcaster.weatherfit.review.api.dto.response.ReviewResponse;
import com.fitcaster.weatherfit.review.domain.entity.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class OrderHistoryItemDto {
    private Long orderItemId;
    private Long itemId;
    private String itemName;
    private int quantity;
    private int itemPrice;
    private String itemImage;
    private ReviewResponse review;

    public static OrderHistoryItemDto from(OrderItem orderItem) {
        OrderHistoryItemDto dto = new OrderHistoryItemDto();
        dto.orderItemId = orderItem.getId();
        dto.itemId = orderItem.getItem().getItemId();
        dto.itemName = orderItem.getItem().getItemName();
        dto.quantity = orderItem.getQuantity();
        dto.itemPrice = orderItem.getItem().getPrice();
        dto.itemImage = orderItem.getItem().getImageURL();

        // 주문(Order) 엔티티에서 사용자(User) 정보를 가져옵니다.
        Long userId = orderItem.getOrder().getUser().getId();

        // 아이템(Item)에 달린 모든 리뷰들(getReviews) 중에서
        // 현재 주문을 한 사용자(userId)가 작성한 리뷰를 찾습니다.
        Optional<Review> reviewOptional = orderItem.getItem().getReviews().stream()
                .filter(review -> review.getUser().getId().equals(userId))
                .findFirst();

        // 찾은 리뷰가 있다면 ReviewResponse 변환하고, 없다면 null을 설정합니다.
        dto.review = reviewOptional.map(ReviewResponse::from).orElse(null);
        return dto;
    }
}