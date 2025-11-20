package com.fitcaster.weatherfit.review.api.dto.response;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ReviewStatisticsDto {
    private final long totalCount;
    private final BigDecimal averageRating;

    public ReviewStatisticsDto(Long totalCount, Double averageRating) {
        this.totalCount = (totalCount != null) ? totalCount : 0L;
        this.averageRating = (averageRating != null) ? BigDecimal.valueOf(averageRating) : BigDecimal.ZERO;
    }
}