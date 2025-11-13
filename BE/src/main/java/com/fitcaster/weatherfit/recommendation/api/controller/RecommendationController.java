package com.fitcaster.weatherfit.recommendation.api.controller;

import com.fitcaster.weatherfit.recommendation.api.dto.AiAllRecommendResponse;
import com.fitcaster.weatherfit.recommendation.api.dto.AiTodayRecommendResponse;
import com.fitcaster.weatherfit.recommendation.api.dto.AiTomorrowRecommendResponse;
import com.fitcaster.weatherfit.recommendation.api.dto.AiWeeklyRecommendResponse;
import com.fitcaster.weatherfit.recommendation.application.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI를 이용해 옷을 추천해주는 컨트롤러
 * @author 김경아
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService; // 추천 관련 비즈니스 로직

    /**
     * 오늘 날씨에 맞는 옷 추천
     * @param address 지역
     * @return 오늘 날씨 기반 추천 받은 옷
     */
    @GetMapping("/today")
    public ResponseEntity<AiTodayRecommendResponse> getTodayRecommendation(@RequestParam String address) {
        AiTodayRecommendResponse todayRecommendation = recommendationService.getTodayRecommendation(address);
        return ResponseEntity.ok(todayRecommendation);
    }

    /**
     * 내일 날씨에 맞는 옷 추천
     * @param address 지역
     * @return 내일 날씨 기반 추천 받은 옷
     */
    @GetMapping("/tomorrow")
    public ResponseEntity<AiTomorrowRecommendResponse> getTomorrowRecommendation(@RequestParam String address) {
        AiTomorrowRecommendResponse todayRecommendation = recommendationService.getTomorrowRecommendation(address);
        return ResponseEntity.ok(todayRecommendation);
    }

    /**
     * 이번주 날씨에 맞는 옷 추천
     * @param address 지역
     * @return 이번주 날씨 기반 추천 받은 옷
     */
    @GetMapping("/weekly")
    public ResponseEntity<AiWeeklyRecommendResponse> getWeeklyRecommendation(@RequestParam String address) {
        AiWeeklyRecommendResponse weeklyRecommendation = recommendationService.getWeeklyRecommendation(address);
        return ResponseEntity.ok(weeklyRecommendation);
    }
}
