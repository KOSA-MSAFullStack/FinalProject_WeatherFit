package com.fitcaster.weatherfit.recommendation.application.port;

import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendRequest;
import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendResponse;

/**
 * AI 요청보내는 인터페이스
 * 서비스에서는 Port에만 의존
 * @author 김경아
 */
public interface AiPort {
    AiRecommendResponse recommendToday(AiRecommendRequest request); // 오늘 옷 추천
}
