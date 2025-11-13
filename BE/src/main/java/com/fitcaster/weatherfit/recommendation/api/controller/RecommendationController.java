package com.fitcaster.weatherfit.recommendation.api.controller;

import com.fitcaster.weatherfit.catalog.api.dto.ItemResponse;
import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendResponse;
import com.fitcaster.weatherfit.recommendation.application.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/today")
    public ResponseEntity<AiRecommendResponse> getTodayRecommendation(@RequestParam String address) {

        AiRecommendResponse todayRecommendation = recommendationService.getTodayRecommendation(address);
        return ResponseEntity.ok(todayRecommendation);

    }
}
