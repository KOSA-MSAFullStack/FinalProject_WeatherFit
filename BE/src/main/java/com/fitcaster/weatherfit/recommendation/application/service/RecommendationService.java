package com.fitcaster.weatherfit.recommendation.application.service;

import com.fitcaster.weatherfit.catalog.application.ItemService;
import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendRequest;
import com.fitcaster.weatherfit.recommendation.api.dto.AiTodayRecommendResponse;
import com.fitcaster.weatherfit.recommendation.api.dto.AiTomorrowRecommendResponse;
import com.fitcaster.weatherfit.recommendation.api.dto.ItemBrief;
import com.fitcaster.weatherfit.recommendation.application.port.AiPort;
import com.fitcaster.weatherfit.weather.api.dto.WeatherResponse;
import com.fitcaster.weatherfit.weather.application.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * 오늘/내일/이번주 날씨 정보를 이용해서 AI에게 옷을 추천받는 비즈니스 로직
 * @author 김경아
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final WeatherService weatherService; // 날씨 관련 비즈니스 로직
    private final ItemService itemService; // 상품 관련 비즈니스 로직
    private final AiPort aiPort; // AI 관련 어댑터

    /**
     * 오늘 날씨 정보를 이용해서 DB에서 1차 분류를 받고 AI에게 옷을 추천 받는 메서드
     * @param address 지역
     */
    public AiTodayRecommendResponse getTodayRecommendation(String address) {
        // 1. 지역에 따른 오늘의 날씨 정보 조회
        WeatherResponse todayWeather = weatherService.getTodayWeather(address);

        // 2. AI에게 보낼 DTO 생성
        AiRecommendRequest aiRecommendRequest = getAiRecommendRequest(todayWeather, address, true);

        // 3. AI에 요청 보내서 응답 받기
        return aiPort.recommendToday(aiRecommendRequest);
    }

    /**
     * 내일 날씨 정보를 이용해서 DB에서 1차 분류를 받고 AI에게 옷을 추천 받는 메서드
     * @param address 지역
     */
    public AiTomorrowRecommendResponse getTomorrowRecommendation(String address) {
        // 1. 지역에 따른 내일의 날씨 정보 조회
        WeatherResponse tomorrowWeather = weatherService.getTomorrowWeather(address);

        // 2. AI에게 보낼 DTO 생성
        AiRecommendRequest aiRecommendRequest = getAiRecommendRequest(tomorrowWeather, address, false);

        // 3. AI에 요청 보내서 응답 받기
        return aiPort.recommendTomorrow(aiRecommendRequest);
    }


    /**
     * 날씨 정보를 이용해서 DB에서 1차 분류를 받고 AI에게 전달할 DTO를 반환하는 메서드
     * @param address 지역
     * @param primary Tier1(true: 기온에 가장 잘 맞는 상위 N개), Tier2(false: 여전히 맞지만 상위 N개 만큼은 아닌 나머지)
     * @return AI에게 전달할 DTO
     */
    private AiRecommendRequest getAiRecommendRequest(WeatherResponse weather, String address, Boolean primary) {
        // 1. month → 계절 결정
        String season = getSeason(weather);

        // 2. 분류와 계절로 옷 1차 분류 + Tier1/Tier2 분리
        List<Item> outers  = findCandidates("OUTER",  season, weather, primary);
        List<Item> tops    = findCandidates("TOP",    season, weather, primary);
        List<Item> bottoms = findCandidates("BOTTOM", season, weather, primary);

        // 3.. AI 프롬프트용 ItemBrief로 변환(AI가 참고할 필드만으로 생성)
        List<ItemBrief> outerBriefs = toBriefs(outers);
        List<ItemBrief> topsBriefs = toBriefs(tops);
        List<ItemBrief> bottomsBriefs = toBriefs(bottoms);

        // 4. AI에게 보낼 DTO 생성
        AiRecommendRequest aiRecommendRequest = toAiRecommendRequest(outerBriefs, topsBriefs, bottomsBriefs, weather);
        return aiRecommendRequest;
    }

    /**
     * AI에게 넘길 옷을 1차 분류하는 메서드
     * @param classification 분류(아우터, 상의, 하의)
     * @param season 계절(봄, 여름, 가을, 겨울)
     * @param weather 날씨 정보
     * @param primary=true  → 상위 N개(Tier1, 오늘용)/primary=false → 나머지(Tier2, 내일용)
     * @return 1차 분류를 마친 상위 20개 후보군
     */
    private List<Item> findCandidates(String classification, String season, WeatherResponse weather, Boolean primary) {
        // 1. 분류 + 계절 기준으로 1차 조회
        List<Item> items = itemService.findByClassificationAndSeason(classification, season);

        // 2. 오늘/내일 평균 기온 계산
        double targetTemp = (weather.getMinTemperature() + weather.getMaxTemperature()) / 2.0;

        // 3. 아이템별 "적정 온도" 계산 후, target과 얼마나 가까운지로 정렬
        List<Item> valid = items.stream()
                // 1. 우선, 기온 범위가 너무 안 맞는 건 걸러내기
                .filter(item -> isTemperatureInRange(item, weather))
                // 2. targetTemp와의 거리 기준으로 정렬
                .sorted((a, b) -> Double.compare(
                        distanceFromTarget(a, targetTemp),
                        distanceFromTarget(b, targetTemp)
                ))
                .toList();

        int size = valid.size();
        int splitIndex = Math.min(10, size); // size가 10보다 작을 수도 있으니 방어

        if (primary) { // 오늘용: 상위 N개
            return valid.subList(0, splitIndex);
        } else {       // 내일용: 나머지(Tier2). 너무 적으면 전체 사용.
            if (size <= splitIndex) {
                return valid; // 후보가 적으면 오늘/내일 동일한 후보군 허용
            }
            return valid.subList(splitIndex, size);
        }
    }

    /**
     * 아이템이 커버하는 온도 구간과 오늘/내일 온도가 겹치는지 체크하는 메서드
     * @param item 옷
     * @param weather 날씨
     * @return 최저
     */
    private boolean isTemperatureInRange(Item item, WeatherResponse weather) {
        double dayMin = weather.getMinTemperature();
        double dayMax = weather.getMaxTemperature();

        return item.getMinTemperature() <= dayMax && item.getMaxTemperature() >= dayMin;
    }

    /**
     * 그 옷이 커버하는 기온의 “중심값”과 타겟 온도(오늘/내일 평균 기온)의 거리를 구하는 메서드
     * 값이 작을수록 → 오늘/내일 기온에 더 잘 맞는 옷
     * @param item 옷
     * @param targetTemp 오늘/내일 평균 기온
     */
    private double distanceFromTarget(Item item, double targetTemp) {
        double itemCenter = (item.getMinTemperature() + item.getMaxTemperature()) / 2.0;
        return Math.abs(itemCenter - targetTemp); // 이 옷이 오늘/내일 기온과 얼마나 차이 나는지
    }

    /**
     * 1차 분류한 item들과 날씨 정보를 담은 DTO로 변환하는 메서드
     * @param outerBriefs AI 프롬프트용 ItemBrief
     * @param topsBriefs AI 프롬프트용 ItemBrief
     * @param bottomsBriefs AI 프롬프트용 ItemBrief
     * @param weather 날씨 정보
     * @return AI에게 전달할 최종 DTO
     */
    private AiRecommendRequest toAiRecommendRequest(List<ItemBrief> outerBriefs, List<ItemBrief> topsBriefs, List<ItemBrief> bottomsBriefs, WeatherResponse weather) {
        AiRecommendRequest aiRecommendRequest = AiRecommendRequest.builder()
                .outers(outerBriefs)
                .tops(topsBriefs)
                .bottoms(bottomsBriefs)
                .weather(weather)
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
                        item.getGender()))
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
