package com.fitcaster.weatherfit.order.api.dto.response;

import com.fitcaster.weatherfit.order.domain.entity.Cart;
import lombok.Builder;
import lombok.Getter;

/**
 * author: 이상우
 * 장바구니 항목 목록 조회 시 클라이언트에 응답하는 DTO
 */
@Getter
@Builder
public class CartItemResponse {

    // 장바구니 자체 정보
    private Long cartId;
    private Integer quantity;   // 담긴 수량
    private Integer totalPrice;    // 항목별 총 금액 (단가 * 수량)

    // 상품 정보 (Item 엔티티에서 추출)
    private Long itemId;
    private String itemName;
    private Integer itemPrice;     // 상품 단가 (판매 가격)
    private String itemImage;   // 상품 이미지 URL



    /**
     * Cart 엔티티를 Response DTO로 변환하는 정적 팩토리 메서드
     */
    public static CartItemResponse from(Cart cart) {
        int unitPrice = cart.getItem().getPrice(); // Item 엔티티에 getPrice()가 있다고 가정

        return CartItemResponse.builder()
                .cartId(cart.getId())
                .itemId(cart.getItem().getItemId())
                .itemName(cart.getItem().getItemName())
                .itemPrice(unitPrice)
                .itemImage(cart.getItem().getImageURL())
                .quantity(cart.getQuantity())
                .totalPrice(unitPrice * cart.getQuantity())
                .build();
    }
}