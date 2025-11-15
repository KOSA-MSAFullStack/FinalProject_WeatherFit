package com.fitcaster.weatherfit.review.domain.entity;

import lombok.Getter;

@Getter
public enum Weather {
    SUNNY("맑음"), CLOUDY("흐림"), WINDY("강풍"), RAINY("비"), SNOWY("눈");
    private final String description;
    Weather(String description) { this.description = description; }
}
