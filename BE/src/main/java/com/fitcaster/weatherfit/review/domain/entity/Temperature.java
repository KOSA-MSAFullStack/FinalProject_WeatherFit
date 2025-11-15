package com.fitcaster.weatherfit.review.domain.entity;

import lombok.Getter;

@Getter
public enum Temperature {
    COLD("추워요"), COOL("시원해요"), NORMAL("보통이에요"), WARM("따뜻해요"), HOT("더워요");
    private final String description;
    Temperature(String description) { this.description = description; }
}
