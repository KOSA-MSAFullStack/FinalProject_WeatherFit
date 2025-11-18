package com.fitcaster.weatherfit.weather.infra;

import com.fitcaster.weatherfit.weather.api.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  OpenWeather One Call 3.0 API를 통해 현재, 오늘, 내일, 이번주 날씨 데이터를 가져오는 클라이언트
 *  OpenWeatherMap API로 오늘/내일/주간의 최저기온, 최고기온, 날씨를 조회
 *  @author 김경아
 */
@Component
@RequiredArgsConstructor
public class OpenWeatherClient {

    @Value("${weather.open-weather.key}")
    private String serviceKey; // application.yml에 정의된 OpenWeatherMap API 인증키를 주입받는다.

    private final WebClient openWeatherWebClient; // OpenWeatherMap API에 요청을 보내고 응답(JSON, XML 등)을 받아오는 역할

    /**
     * 오늘 날씨 정보 추출
     */
    public WeatherResponse getTodayWeather(double lat, double lon) {
        Map<String, Object> data = requestWeatherData(lat, lon);
        return parseWeatherData(data, 0); // daily[0] = 오늘
    }

    /**
     * 내일 날씨 정보 추출
     */
    public WeatherResponse getTomorrowWeather(double lat, double lon) {
        Map<String, Object> data = requestWeatherData(lat, lon);
        return parseWeatherData(data, 1); // daily[1] = 내일
    }

    /**
     * 이번주 (오늘 이후 7일치) 날씨 목록 추출
     */
    public List<WeatherResponse> getWeeklyWeather(double lat, double lon) {
        Map<String, Object> data = requestWeatherData(lat, lon);
        List<Map<String, Object>> dailyList = (List<Map<String, Object>>) data.get("daily");

        List<WeatherResponse> result = new ArrayList<>();
        for (int i = 0; i < dailyList.size(); i++) {
            result.add(parseWeatherData(data, i));
        }
        return result;
    }

    /**
     * 위도(lat), 경도(lon)를 기반으로 One Call 3.0 API 호출
     * @param lat 위도
     * @param lon 경도
     * @return 응답 JSON 전체를 Map 형태로 반환
     */
    public Map<String, Object> requestWeatherData(double lat, double lon) {

        // 1. 요청 url 생성
        // metric 단위(섭씨), lang=kr 설정
        String url = String.format(
                "/onecall?lat=%f&lon=%f&units=metric&lang=kr&appid=%s",
                lat, lon, serviceKey
        );

        // 2.WebClient로 외부 API 호출
        Map<String, Object> response = openWeatherWebClient.get()
                .uri(url)              // 요청할 주소
                .retrieve()            // 서버로 요청 전송
                .bodyToMono(Map.class) // 응답 본문을 JSON -> Map으로 변환
                .block();              // 비동기 결과를 동기적으로 처리하기 위함

        if (response == null || !response.containsKey("daily")) {
            throw new IllegalStateException("OpenWeather 응답이 올바르지 않습니다.");
        }

        return response;
    }

    /***
     * OpenWeather의 daily[n] 데이터를 WeatherResponse 객체로 변환하는 공통 파싱 메서드
     * @param response One Call API 전체 응답 Map
     * @param index daily 배열 인덱스 (0: 오늘, 1: 내일, 2~7: 이후 일자)
     * @return 날짜, 최저기온, 최고기온, 날씨 정보를 담은 WeatherResponse DTO
     */
    private WeatherResponse parseWeatherData(Map<String, Object> response, int index) {
        // daily 배열 가져오기
        List<Map<String, Object>> dailyList = (List<Map<String, Object>>) response.get("daily");
        int timezoneOffset = ((Number) response.get("timezone_offset")).intValue();

        // 인덱스 범위 체크
        if (index >= dailyList.size()) {
            throw new IllegalArgumentException("daily[" + index + "] 데이터가 존재하지 않습니다.");
        }

        // 하루치 데이터 추출
        Map<String, Object> day = dailyList.get(index); // 0:오늘, 1:내일, 2~7: 주간
        Map<String, Object> temp = (Map<String, Object>) day.get("temp"); // 기온
        List<Map<String, Object>> weatherList = (List<Map<String, Object>>) day.get("weather"); // 날씨 상태

        // dt(UTC 기준 timestamp) + timezone_offset → LocalDate로 변환
        long dt = ((Number) day.get("dt")).longValue();
        Instant instant = Instant.ofEpochSecond(dt + timezoneOffset); // 한국시간으로 변환
        LocalDate date = instant.atZone(ZoneId.of("Asia/Seoul")).toLocalDate();

        // 기온 및 날씨 설명 추출
        double minTemp = ((Number) temp.get("min")).doubleValue();         // 최저기온
        double maxTemp = ((Number) temp.get("max")).doubleValue();         // 최고기온

        // 날씨 상태 id 값
        int code = ((Number) weatherList.get(0).get("id")).intValue();
        String condition = getWeatherCondition(code); // 날씨 id로 상태 추출
        String icon = (String) weatherList.get(0).get("icon");

        // 변환한 데이터를 DTO로 반환
        return WeatherResponse.builder()
                .date(date)
                .minTemperature(minTemp)
                .maxTemperature(maxTemp)
                .condition(condition)
                .icon(icon)
                .build();
    }

    /**
     * weather 배열의 id 값으로 날씨 상태 변환
     * @param code weather의 id 값
     * @return 날씨 상태
     */
    private String getWeatherCondition(int code) {
        String status = "";
        switch (code / 100) {
            case 2: // 2xx: Thunderstorm
                status = "천둥/번개";
                break;
            case 3: // 3xx: Drizzle
                status = "이슬비";
                break;
            case 5: // 5xx: Rain
                status = "비";
                break;
            case 6: // 6xx: Snow
                status = "눈";
                break;
            case 7: // 7xx: 안개/먼지 등
                status = "안개/먼지";
                break;
            case 8: // 800~804
                if (code == 800) {
                    status = "맑음";
                } else if (code / 10 == 80) {
                    status = "구름";
                }
                break;
            default:
                break;
        }
        return status;
    }
}

