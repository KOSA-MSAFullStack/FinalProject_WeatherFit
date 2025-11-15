package com.fitcaster.weatherfit.order.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * author: 이상우
 * 장바구니 항목의 수량을 변경할 때 클라이언트에서 받는 요청 DTO
 */
@Getter
@NoArgsConstructor
public class CartUpdateQuantityRequest {

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    private Integer quantity;
}