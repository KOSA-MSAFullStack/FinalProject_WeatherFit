package com.fitcaster.weatherfit.order.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * author: 이상우
 * 장바구니에 상품을 추가할 때 클라이언트에서 받는 요청 DTO
 */
@Getter
@NoArgsConstructor
public class CartAddItemRequest {

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long itemId;

}