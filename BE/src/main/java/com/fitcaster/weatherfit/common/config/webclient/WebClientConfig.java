package com.fitcaster.weatherfit.common.config.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 외부 API(기상청, 카카오 등) 호출에 사용할 WebClient Bean들을 등록하는 설정 클래스
 * @author 김경아
 */
@Configuration
public class WebClientConfig {

    /**
     * 기상청 API 호출용 WebClient Bean
     * @param builder Spring이 자동으로 주입하는 WebClient.Builder 객체
     * @return 기상청 API 기본 URL("http://apis.data.go.kr")을 가진 WebClient 인스턴스
     */
    @Bean(name = "kmaWebClient")
    public WebClient kmaWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://apis.data.go.kr") // 기상청 API 기본 baseUrl
                .build();
    }

    /**
     * OpenWeatherMap API 호출용 WebClient Bean
     * @param builder Spring이 자동으로 주입하는 WebClient.Builder 객체
     * @return OpenWeatherMap API 기본 URL("https://api.openweathermap.org/data/3.0")을 가진 WebClient 인스턴스
     */
    @Bean(name = "openWeatherWebClient")
    public WebClient openWeatherWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://api.openweathermap.org/data/3.0")
                .build();
    }

    /**
     * 카카오 지도 API 호출용 WebClient Bean
     * @param builder     Spring이 자동으로 주입하는 WebClient.Builder 객체
     * @param kakaoApiKey application.yml에서 주입받은 Kakao REST API Key
     * @return 카카오 API 기본 URL("https://dapi.kakao.com")과 인증 헤더를 포함한 WebClient 인스턴스
     */
    @Bean(name = "kakaoWebClient")
    public WebClient kakaoWebClient(WebClient.Builder builder, @Value("${kakao.api.key}") String kakaoApiKey) {
        return builder
                .baseUrl("https://dapi.kakao.com") // 카카오 로컬 API 기본 baseUrl
                .defaultHeader("Authorization", "KakaoAK " + kakaoApiKey) // 인증 헤더 설정
                .build();
    }
}
