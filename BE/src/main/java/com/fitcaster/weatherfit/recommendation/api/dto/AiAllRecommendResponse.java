package com.fitcaster.weatherfit.recommendation.api.dto;

/**
 * 메인 페이지에서 한번에 오늘, 내일, 이번주 옷을 추천 받는 DTO
 * @author 김경아
 */
public record AiAllRecommendResponse(
        AiTodayRecommendResponse today,
        AiTomorrowRecommendResponse tomorrow,
        AiWeeklyRecommendResponse weekly
) {}
