package com.fitcaster.weatherfit.weather.api.controller;

import com.fitcaster.weatherfit.weather.api.dto.WeatherResponse;
import com.fitcaster.weatherfit.weather.application.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 날씨에 대한 정보를 요청하는 컨트롤러
 * @author 김경아
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService; // 날씨 비즈니스 로직을 수행하는 서비스

    /**
     * 오늘 날씨에 대한 정보를 불러오는 메서드
     * @param region 사용자가 검색한 지역
     * @return 오늘 최저 기온, 최고 기온, 날씨
     */
    @GetMapping("/today")
    public WeatherResponse getTodayWeather(@RequestParam String region) {
        return weatherService.getTodayWeather(region);
    }

    @GetMapping("/tomorrow")
    public WeatherResponse getTommorwWeather(@RequestParam String region) {
        return weatherService.getTomorrowWeather(region);
    }

    @GetMapping("/weekly")
    public List<WeatherResponse> getWeeklyWeather(@RequestParam String region) {
        return weatherService.getWeeklyWeather(region);
    }
}
