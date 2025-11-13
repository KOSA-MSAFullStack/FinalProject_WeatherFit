package com.fitcaster.weatherfit.weather.infra;

import com.fitcaster.weatherfit.weather.api.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 *  기상청 API 서버와 통신하는 클라이언트
 *  기상청 API로 오늘/내일의 최저기온, 최고기온, 날씨를 조회
 *  @author 김경아
 */
@Component
@RequiredArgsConstructor
public class KmaWeatherClient {

    @Value("${weather.kma.key}")
    private String serviceKey; // application.yml에 정의된 기상청 API 인증키를 주입받는다.

    private final WebClient kmaWebClient; // 기상청 API에 요청을 보내고 응답(JSON, XML 등)을 받아오는 역할

    /**
     * 기상청 단기예보(OpenAPI) 서버에 GET 요청을 보내고 응답(JSON)을 Map 형태로 반환하는 메서드
     * @param baseDate 발표 기준 날짜 (yyyyMMdd)
     * @param baseTime 발표 기준 시각 (0200)
     * @param nx 예보지점 X좌표
     * @param ny 예보지점 Y좌표
     * @return 기상청 API 응답 Map
     */
    private Map<String, Object> requestWeatherData(String baseDate, String baseTime, int nx, int ny) {
        // 1. API 요청 URL 생성
        String url = String.format(
                "/1360000/VilageFcstInfoService_2.0/getVilageFcst" +
                        "?serviceKey=%s" +   // 서비스키
                        "&numOfRows=200" +   // 한 페이지 결과 수
                        "&pageNo=1" +        // 페이지 번호
                        "&dataType=JSON" +   // 요청자료형식
                        "&base_date=%s" +    // 발표 날짜
                        "&base_time=%s" +    // 발표 시간
                        "&nx=%d&ny=%d",      // 예보지점의 좌표(nx, ny)
                URLEncoder.encode(serviceKey, StandardCharsets.UTF_8), baseDate, baseTime, nx, ny
        );

        // 2. WebClient를 이용하여 GET 요청 전송
        Map<String, Object> response =  kmaWebClient.get() // HTTP GET 요청
                .uri(url)              // 요청할 주소
                .retrieve()            // 서버로 요청 전송
                .bodyToMono(Map.class) // 응답 본문을 JSON -> Map으로 변환
                .block();              // 비동기 결과를 동기적으로 처리하기 위함

        return response;
    }

    /***
     * 기상청 단기예보 API 응답(Map)에서 특정 날짜의 예보 데이터(최저기온, 최고기온, 날씨)를 추출하여 WeatherResponse DTO로 변환하는 메서드
     * @param response 기상청 API 응답 Map
     * @param targetDate 추출 대상 날짜 (yyyyMMdd) (오늘/내일)
     * @return 해당 날짜의 최저기온, 최고기온, 날씨 정보를 담은 WeatherResponse DTO
     */
    private WeatherResponse parseWeatherData(Map<String, Object> response, String targetDate) {
        // JSON 응답 구조: response(Map) → body(Map) → items(Map) → item[](List)
        Map<String, Object> responseBody =
                (Map<String, Object>) ((Map<String, Object>) response.get("response")).get("body");

        if (responseBody == null) {
            throw new IllegalStateException("기상청 응답 body가 비어있습니다.");
        }

        Map<String, Object> itemsMap = (Map<String, Object>) responseBody.get("items");

        if (itemsMap == null) {
            throw new IllegalStateException("기상청 응답에 items가 없습니다.");
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) itemsMap.get("item"); // 배열

        // 필요한 데이터만 추출할 변수 초기화
        Double minTemperature = null;     // 최저기온 (TMN)
        Double maxTemperature = null;     // 최고기온 (TMX)
        String condition = "";            // 기본 날씨 상태

        // 각 항목을 순회하면서 필요한 데이터(TMN, TMX, SKY, PTY)만 추출
        for (Map<String, Object> item : items) {
            String category = (String) item.get("category");  // 예보 항목 코드
            String value = (String) item.get("fcstValue");    // 예보 값
            String fcstDate = (String) item.get("fcstDate");  // 예보 날짜

            if (!fcstDate.equals(targetDate)) continue; // 해당 날짜 데이터만 필터링

            switch (category) {
                case "TMN": // 최저기온
                    minTemperature = Double.parseDouble(value);
                    break;
                case "TMX": // 최고기온
                    maxTemperature = Double.parseDouble(value);
                    break;
                case "SKY": // 하늘상태 코드
                    condition = skyToCondition(value);
                    break;
                case "PTY": // 강수형태 코드
                    // PTY가 0이 아니면(비, 눈, 소나기 등) 하늘상태보다 우선 적용
                    if (!value.equals("0")) {
                        condition = rainOrSnow(value);
                    }
                    break;
            }
        }

        // 5. 최저기온, 최고기온, 날씨 상태를 담은 응답 DTO로 반환
        return WeatherResponse.builder()
                .minTemperature(minTemperature)
                .maxTemperature(maxTemperature)
                .condition(condition)
                .build();
    }

    /**
     * 오늘 날씨 정보를 가져오는 메서드
     * @param nx 예보지점 X좌표
     * @param ny 예보지점 Y좌표
     * @return 내일의 최저기온, 최고기온, 날씨
     */
    public WeatherResponse getTodayWeather(int nx, int ny) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 오늘 날짜
        Map<String, Object> response = requestWeatherData(today, "0200", nx, ny); // 기상청 API에 요청을 보내서 오늘 날씨 정보를 Map으로 받는다
        return parseWeatherData(response, today); // Map으로 받은 정보를 DTO로 변환한다
    }

    /**
     * 내일 날씨 정보를 가져오는 메서드
     * @param nx 예보지점 X좌표
     * @param ny 예보지점 Y좌표
     * @return 내일의 최저기온, 최고기온, 날씨
     */
    public WeatherResponse getTomorrowWeather(int nx, int ny) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 오늘날짜
        String tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 내일 날짜
        Map<String, Object> response = requestWeatherData(today, "0200", nx, ny); // 기상청 API에 요청을 보내서 내일 날씨 정보를 Map으로 받는다
        return parseWeatherData(response, tomorrow); // Map으로 받은 정보를 DTO로 변환한다
    }


    /**
     * SKY 코드(하늘 상태)를 한글 텍스트로 변환하는 메서드
     * 1: 맑음, 3: 구름많음, 4: 흐림
     */
    private String skyToCondition(String code) {
        switch (code) {
            case "1": return "맑음";
            case "3": return "구름많음";
            case "4": return "흐림";
            default: return "알 수 없음";
        }
    }

    /**
     * PTY 코드(강수 형태)를 한글 텍스트로 변환하는 메서드
     * 1: 비, 2: 비 또는 눈, 3: 눈, 4: 소나기
     * 비나 눈이 내리는 경우 SKY 코드보다 이 값이 우선 적용된다.
     */
    private String rainOrSnow(String code) {
        switch (code) {
            case "1": return "비";
            case "2": return "비 또는 눈";
            case "3": return "눈";
            case "4": return "소나기";
            default: return "맑음";
        }
    }
}

