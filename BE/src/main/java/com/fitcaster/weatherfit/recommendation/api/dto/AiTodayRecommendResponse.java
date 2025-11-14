package com.fitcaster.weatherfit.recommendation.api.dto;

/**
 * AI에게 추천받은 오늘 날씨에 맞는 아우터 1개, 상의 1개, 하의 1개
 * @author 김경아
 */
public record AiTodayRecommendResponse(
    ItemCard outer,
    ItemCard top,
    ItemCard bottom
    ){}
