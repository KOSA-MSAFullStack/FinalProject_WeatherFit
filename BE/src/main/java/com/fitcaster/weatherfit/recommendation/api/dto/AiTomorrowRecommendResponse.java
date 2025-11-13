package com.fitcaster.weatherfit.recommendation.api.dto;

/**
 * AI에게 추천받은 내일 날씨에 맞는 옷(아우터, 상의, 하의 중 3개)
 * @author 김경아
 */
public record AiTomorrowRecommendResponse(
    ItemCard firstItem,
    ItemCard secondItem,
    ItemCard thirdItem
    ){}
