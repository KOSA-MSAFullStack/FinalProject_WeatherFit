package com.fitcaster.weatherfit.weather.application;

import com.fitcaster.weatherfit.common.util.GeographicTranslator;
import com.fitcaster.weatherfit.weather.api.dto.WeatherResponse;
import com.fitcaster.weatherfit.weather.infra.KakaoLocationClient;
import com.fitcaster.weatherfit.weather.infra.WeatherClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 날씨 관련 비즈니스 로직 처리
 * @author 김경아
 */
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherClient weatherClient; // 기상청 API를 호출하는 클라이언트
    private final KakaoLocationClient kakaoLocationClient; // 카카오 지도 API를 호출하는 클라이언트
    private final GeographicTranslator geographicTranslator; // 위도, 경도를 격자로 반환하는 변환 클래스

    /**
     * 기상청 API를 호출해서 오늘의 날씨 정보를 조회하고 DTO에 맞게 반환
     * @return 오늘 최저 기온, 최고 기온, 날씨
     */
    public WeatherResponse getTodayWeather(String address) {
        // 사용자가 검색한 지역의 좌표
        int[] grid = getGrid(address);

        // nx, ny를 기반으로 오늘의 날씨를 가져온다.
        return weatherClient.getTodayWeather(grid[0], grid[1]);
    }

    /**
     * 사용자가 검색한 지역의 좌표를 얻는 메서드
     * @param address 사용자가 검색한 지역
     * @return nx, ny를 담은 배열
     */
    private int[] getGrid(String address) {
        // 1. 사용자가 검색한 지역을 위도, 경도로 변환
        double[] latLng = kakaoLocationClient.getLatLng(address);

        // 2. 위도, 경도를 기상청에서 사용자는 격자(nx, ny)로 변환
        int[] grid = geographicTranslator.convert(latLng[0], latLng[1]); // 위도, 경도

        return grid;
    }

}
