package com.fitcaster.weatherfit.review.api.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ReviewRequest(
        @NotNull(message = "상품 ID는 필수입니다.")
        Long itemId,

        @NotNull(message = "별점은 필수입니다.")
        @DecimalMin(value = "0.5", message = "별점은 0.5 이상이어야 합니다.")
        @DecimalMax(value = "5.0", message = "별점은 5.0 이하이어야 합니다.")
        BigDecimal score,

        @NotBlank(message = "날씨 정보는 필수입니다.")
        String weather,

        @NotBlank(message = "날씨 체감 정보는 필수입니다.")
        String weatherSuitability,

        @NotBlank(message = "실내 착용감 정보는 필수입니다.")
        String breathability,

        @NotBlank(message = "리뷰 내용은 필수입니다.")
        @Size(max = 1000, message = "리뷰 내용은 1000자를 초과할 수 없습니다.")
        String content
) {}