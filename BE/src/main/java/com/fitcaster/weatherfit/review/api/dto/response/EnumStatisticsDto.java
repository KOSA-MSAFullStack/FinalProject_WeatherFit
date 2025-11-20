package com.fitcaster.weatherfit.review.api.dto.response;

import lombok.Getter;

@Getter
public class EnumStatisticsDto<T> {
    private final T enumValue;
    private final long count;

    public EnumStatisticsDto(T enumValue, long count) {
        this.enumValue = enumValue;
        this.count = count;
    }
}