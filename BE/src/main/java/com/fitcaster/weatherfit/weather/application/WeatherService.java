package com.fitcaster.weatherfit.weather.application;

import com.fitcaster.weatherfit.common.util.GeographicTranslator;
import com.fitcaster.weatherfit.weather.api.dto.WeatherResponse;
import com.fitcaster.weatherfit.weather.infra.KakaoLocationClient;
import com.fitcaster.weatherfit.weather.infra.KmaWeatherClient;
import com.fitcaster.weatherfit.weather.infra.OpenWeatherClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 날씨 관련 비즈니스 로직 처리
 * @author 김경아
 */
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final KmaWeatherClient kmaWeatherClient; // 기상청 API를 호출하는 클라이언트
    private final OpenWeatherClient openWeatherClient; // OpenWeatherMap API를 호출하는 클라이언트
    private final KakaoLocationClient kakaoLocationClient; // 카카오 지도 API를 호출하는 클라이언트
    private final GeographicTranslator geographicTranslator; // 위도, 경도를 격자로 반환하는 변환 클래스

    /**
     * 검색 지역의 내일 날씨 정보를 조회
     * @return 오늘 최저 기온, 최고 기온, 날씨를 담은 WeatherResponse DTOㄴ
     */
    public WeatherResponse getTodayWeather(String address) {
        // 1. 검색한 지역의 위도, 경도를 반환
        double[] latLng = kakaoLocationClient.getLatLng(address);
        return openWeatherClient.getTodayWeather(latLng[0], latLng[1]);
    }

    /**
     * 검색 지역의 내일 날씨 정보를 조회
     * @return 내일 최저 기온, 최고 기온, 날씨를 담은 WeatherResponse DTO
     */
    public WeatherResponse getTomorrowWeather(String address) {
        // 1. 검색한 지역의 위도, 경도를 반환
        double[] latLng = kakaoLocationClient.getLatLng(address);

        // 2. 위도, 경도 기준으로 내일의 날씨를 가져온다
        return openWeatherClient.getTomorrowWeather(latLng[0], latLng[1]);
    }

    /**
     * 검색 지역의 이번주 날씨 정보를 조회
     * @return 내일 최저 기온, 최고 기온, 날씨를 담은 WeatherResponse DTO 배열을 반환
     */
    public List<WeatherResponse> getWeeklyWeather(String address) {
        // 1. 검색한 지역의 위도, 경도를 반환
        double[] latLng = kakaoLocationClient.getLatLng(address);

        // 2. 위도, 경도 기준으로 내일의 날씨를 가져온다
        return openWeatherClient.getWeeklyWeather(latLng[0], latLng[1]);
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
