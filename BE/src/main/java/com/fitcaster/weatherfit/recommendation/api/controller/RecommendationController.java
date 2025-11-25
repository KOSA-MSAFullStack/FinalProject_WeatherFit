package com.fitcaster.weatherfit.recommendation.api.controller;

import com.fitcaster.weatherfit.mypage.api.dto.response.ProfileResponse;
import com.fitcaster.weatherfit.mypage.application.ProfileService;
import com.fitcaster.weatherfit.recommendation.api.dto.AiTodayRecommendResponse;
import com.fitcaster.weatherfit.recommendation.api.dto.AiTomorrowRecommendResponse;
import com.fitcaster.weatherfit.recommendation.api.dto.AiWeeklyRecommendResponse;
import com.fitcaster.weatherfit.recommendation.application.service.RecommendationService;
import com.fitcaster.weatherfit.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final ProfileService profileService; // 개인 정보 서비스
    /**
     * 오늘 날씨에 맞는 옷 추천
     * @param region 지역
     * @return 오늘 날씨 기반 추천 받은 옷
     */
    @GetMapping("/today")
    public ResponseEntity<AiTodayRecommendResponse> getTodayRecommendation(@RequestParam String region, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        AiTodayRecommendResponse todayRecommendation = recommendationService.getTodayRecommendation(region, userId);
        return ResponseEntity.ok(todayRecommendation);
    }

    /**
     * 내일 날씨에 맞는 옷 추천
     * @param region 지역
     * @return 내일 날씨 기반 추천 받은 옷
     */
    @GetMapping("/tomorrow")
    public ResponseEntity<AiTomorrowRecommendResponse> getTomorrowRecommendation(@RequestParam String region, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        AiTomorrowRecommendResponse todayRecommendation = recommendationService.getTomorrowRecommendation(region, userId);
        return ResponseEntity.ok(todayRecommendation);
    }

    /**
     * 이번주 날씨에 맞는 옷 추천
     * @param region 지역
     * @return 이번주 날씨 기반 추천 받은 옷
     */
    @GetMapping("/weekly")
    public ResponseEntity<AiWeeklyRecommendResponse> getWeeklyRecommendation(@RequestParam String region, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        AiWeeklyRecommendResponse weeklyRecommendation = recommendationService.getWeeklyRecommendation(region, userId);
        return ResponseEntity.ok(weeklyRecommendation);
    }
}
