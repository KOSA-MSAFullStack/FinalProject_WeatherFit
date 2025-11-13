package com.fitcaster.weatherfit.weather.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 날씨 정보를 받아오는 응답 Dto
 * @author 김경아
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponse {
    private LocalDate date;            // 날짜
    private Double minTemperature;     // 최저기온
    private Double maxTemperature;     // 최고기온
    private String condition;          // 날씨 설명 (예: 맑음, 흐림, 튼구름 등)
}
