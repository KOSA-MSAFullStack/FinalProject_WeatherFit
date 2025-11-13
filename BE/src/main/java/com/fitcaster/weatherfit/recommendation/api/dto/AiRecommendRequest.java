package com.fitcaster.weatherfit.recommendation.api.dto;

import com.fitcaster.weatherfit.weather.api.dto.WeatherResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiRecommendRequest {
    private List<ItemBrief> outers;          // 아우터 목록
    private List<ItemBrief> tops;            // 상의 목록 
    private List<ItemBrief> bottoms;         // 하의 목록   
    private WeatherResponse weatherResponse; // 날씨 정보
}
