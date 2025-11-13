package com.fitcaster.weatherfit.recommendation.application.service;

import com.fitcaster.weatherfit.catalog.application.ItemService;
import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendRequest;
import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendResponse;
import com.fitcaster.weatherfit.recommendation.api.dto.ItemBrief;
import com.fitcaster.weatherfit.recommendation.application.port.AiPort;
import com.fitcaster.weatherfit.weather.api.dto.WeatherResponse;
import com.fitcaster.weatherfit.weather.application.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final WeatherService weatherService; // 날씨 관련 비즈니스 로직
    private final ItemService itemService; // 상품 관련 비즈니스 로직
    private final AiPort aiPort; // AI 관련 어댑터
    
    /**
     *
     * @param address
     */
    public AiRecommendResponse getTodayRecommendation(String address) {
        // 1. 지역에 따른 오늘의 날씨 정보 조회
        WeatherResponse todayWeather = weatherService.getTodayWeather(address);

        // 2. month → 계절 결정
        String season = getSeason(todayWeather);

        // 3. 분류와 계절로 옷 1차 분류
        List<Item> outers  = itemService.findByClassificationAndSeason("OUTER",  season);
        List<Item> tops    = itemService.findByClassificationAndSeason("TOP",    season);
        List<Item> bottoms = itemService.findByClassificationAndSeason("BOTTOM", season);

        // 3-2. AI 프롬프트용 ItemBrief로 변환(AI가 참고할 필드만으로 생성)
        List<ItemBrief> outerBriefs = toBriefs(outers);
        List<ItemBrief> topsBriefs = toBriefs(tops);
        List<ItemBrief> bottomsBriefs = toBriefs(bottoms);

        // 4. AI에게 보낼 DTO 생성
        AiRecommendRequest aiRecommendRequest = toAiRecommendRequest(outerBriefs, topsBriefs, bottomsBriefs, todayWeather);

        // 4. AI에 요청 보내서 응답 받기
        AiRecommendResponse aiRecommendResponse = aiPort.recommendToday(aiRecommendRequest);

        return aiRecommendResponse;

    }

    /**
     * 1차 분류한 item들과 날씨 정보를 담은 DTO로 변환하는 메서드
     * @param outerBriefs AI 프롬프트용 ItemBrief
     * @param topsBriefs AI 프롬프트용 ItemBrief
     * @param bottomsBriefs AI 프롬프트용 ItemBrief
     * @param weatherResponse 날씨 정보
     * @return AI에게 전달할 최종 DTO
     */
    private AiRecommendRequest toAiRecommendRequest(List<ItemBrief> outerBriefs, List<ItemBrief> topsBriefs, List<ItemBrief> bottomsBriefs, WeatherResponse weatherResponse) {
        AiRecommendRequest aiRecommendRequest = AiRecommendRequest.builder()
                .outers(outerBriefs)
                .tops(topsBriefs)
                .bottoms(bottomsBriefs)
                .weatherResponse(weatherResponse)
                .build();
        return aiRecommendRequest;
    }

    /**
     * Item 엔티티로 받은 데이터를 ItemBrief로 변경하는 메서드
     * @param items 1차 분류한 item 배열
     * @return AI에게 전달할 필드만으로 구성된 DTO
     */
    private List<ItemBrief> toBriefs(List<Item> items) {
        return items.stream()
                .map(item -> new ItemBrief(
                        item.getItemId(),
                        item.getItemName(),
                        item.getCategory().getCategory(),
                        item.getMinTemperature(),
                        item.getMaxTemperature(),
                        item.getGender(),
                        item.getImageURL()))
                .toList();
    }

    /**
     * 오늘 날짜의 달로 계절 계산하는 메서드
     * @param weather 날씨 정보
     * @return 계절
     */
    private String getSeason(WeatherResponse weather) {
        int month = weather.getDate().getMonthValue(); // 오늘날짜의 달

        if (month >= 3 && month <= 5)  return "봄";  // 3, 4, 5
        if (month >= 6 && month <= 8)  return "여름"; // 6, 7, 8
        if (month >= 9 && month <= 11) return "가을"; // 9, 10, 11
        return "겨울"; // 12, 1, 2
    }
}
