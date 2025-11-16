package com.fitcaster.weatherfit.order.api.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 주문 생성 요청 DTO (장바구니 항목 ID 리스트를 받음)
 */
@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    // 장바구니 항목 ID 리스트
    @NotEmpty(message = "주문할 장바구니 항목 ID는 필수입니다.")
    private List<Long> cartItemIds;

}
