package com.fitcaster.weatherfit.recommendation.application.port;

import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendRequest;
import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendResponse;

public interface AiPort {
    AiRecommendResponse recommendToday(AiRecommendRequest request); // 오늘 옷 추천
}
