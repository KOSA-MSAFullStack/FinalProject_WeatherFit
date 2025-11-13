package com.fitcaster.weatherfit.recommendation.api.dto;

/**
 * AI에게 추천받은 이번주 날씨에 맞는 옷(아우터, 상의, 하의 중 3개)
 * @author 김경아
 */
public record AiWeeklyRecommendResponse(
        ItemCard firstItem,
        ItemCard secondItem,
        ItemCard thirdItem
) {}
