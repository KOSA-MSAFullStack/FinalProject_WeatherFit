package com.fitcaster.weatherfit.recommendation.application.port;

import com.fitcaster.weatherfit.recommendation.api.dto.AiAllRecommendResponse;
import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendRequest;
import com.fitcaster.weatherfit.recommendation.api.dto.AiTodayRecommendResponse;
import com.fitcaster.weatherfit.recommendation.api.dto.AiTomorrowRecommendResponse;

/**
 * AI 요청보내는 인터페이스
 * 서비스에서는 Port에만 의존
 * @author 김경아
 */
public interface AiPort {
    AiTodayRecommendResponse recommendToday(AiRecommendRequest request);    // 오늘 옷 추천
    AiTomorrowRecommendResponse recommendTomorrow(AiRecommendRequest request); // 내일 옷 추천

    // 메인페이지용: 오늘/내일/이번주를 한 번에 추천
//    AiAllRecommendResponse recommendAll(AiRecommendRequest request);
}
