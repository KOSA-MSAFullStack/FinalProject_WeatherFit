package com.fitcaster.weatherfit.recommendation.api.dto;

public record AiRecommendResponse (
    ItemBrief outer,
    ItemBrief top,
    ItemBrief bottom
    ){}
