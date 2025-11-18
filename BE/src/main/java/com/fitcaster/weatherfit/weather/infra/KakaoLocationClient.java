package com.fitcaster.weatherfit.weather.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Kakao 지도 API 서버와 통신하는 클라이언트
 * @author 김경아
 */
@Component
@RequiredArgsConstructor
public class KakaoLocationClient {

    @Value("${kakao.api.key}")
    private String kakaoApiKey; // application.yml 에 정의된 카카오 REST API 키를 주입받습니다.

    private final WebClient kakaoWebClient; // 외부 서버에 요청을 보내고 응답(JSON, XML 등)을 받아오는 역할

    /**
     * 주소 문자열(예: "서울 강남구 역삼동")을 전달받아
     * 카카오 로컬 API로 요청을 보내고, 해당 주소의 위도/경도 좌표를 반환하는 메서드
     * @param address 주소
     * @return 위도(lat), 경도(lng)
     */
    public double[] getLatLng(String address) {
        // 1. 카카오 API 호출 -> 주소 기반 검색 시도
        String url = String.format("/v2/local/search/address.json?query=%s",
                address);

        Map<String, Object> response = kakaoWebClient.get()
                .uri(url)
                .retrieve() // 응답을 Map 구조(JSON → Map)로 받음
                .bodyToMono(Map.class) // 응답 본문을 JSON -> Map으로 변환
                .block(); // 동기식 처리

        // 응답에서 주소 결과 목록 추출
        List<Map<String, Object>> documents = (List<Map<String, Object>>) response.get("documents");

//        // 2. 주소 검색 결과가 없으면 키워드 검색으로 대체
//        url = String.format("/v2/local/search/keyword.json?query=%s",
//                address);
//
//        if (documents == null || documents.isEmpty()) {
//            response = kakaoWebClient.get()
//                    .uri(url)
//                    .retrieve()
//                    .bodyToMono(Map.class)
//                    .block();
//
//            documents = (List<Map<String, Object>>) response.get("documents");
//        }

        if (documents == null || documents.isEmpty()) {
            // 검색된 주소가 없을 경우 예외 발생
            throw new IllegalArgumentException("해당 주소를 찾을 수 없습니다: " + address);
        }

        // 첫 번째 결과(가장 일치하는 주소)의 좌표 정보 추출
        Map<String, Object> location = documents.get(0);
        double lat = Double.parseDouble((String) location.get("y")); // 위도
        double lng = Double.parseDouble((String) location.get("x")); // 경도

        // [위도, 경도] 형태로 반환
        return new double[]{lat, lng};
    }
}
