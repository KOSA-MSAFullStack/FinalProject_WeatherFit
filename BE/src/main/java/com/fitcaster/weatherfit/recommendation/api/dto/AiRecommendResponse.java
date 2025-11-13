package com.fitcaster.weatherfit.recommendation.api.dto;

/**
 * AI에게 추천받은 내용을 담은 DTO
 * @author 김경아
 */
public record AiRecommendResponse (
    ItemBrief outer,
    ItemBrief top,
    ItemBrief bottom
    ){}
