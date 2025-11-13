package com.fitcaster.weatherfit.recommendation.infra;

import com.fitcaster.weatherfit.recommendation.api.dto.*;
import com.fitcaster.weatherfit.recommendation.application.port.AiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Component;

/**
 * AI Port를 실제 구현한 부분
 * Open AI 모델을 이용해서 AI 요청을 보내는 부분
 * @author 김경아
 */
@Component
@RequiredArgsConstructor
public class OpenAiAdapter implements AiPort {

    private final ChatClient chatClient;         // Spring AI가 제공하는 LLM(대형 언어 모델) 대화 API 클라이언트
    private final PromptTemplate promptTemplate; // 프롬프트 작성 클래스

    private static final String SYSTEM_PROMPT = """
      당신은 날씨에 따른 옷을 추천해주는 쇼핑몰의 코디 추천 어시스턴트입니다./n
      불필요한 텍스트/설명/마크다운은 금지합니다./n
      키는 outer, top, bottom 만 허용합니다./n
    """;

    /**
     * 오늘 날씨에 대한 옷 추천을 받는 메서드
     * @param request AI에게 넘겨줄 정보를 담은 Dto
     * @return 오늘 날씨 기반 추천 옷(아우터 1개, 상의 1개, 하의 1개)
     */
    @Override
    public AiTodayRecommendResponse recommendToday(AiRecommendRequest request) {
        // 1. 프롬프트 문자열 생성
        String userPrompt = promptTemplate.buildTodayRecommendPrompt(request);

        // 2. AI에게 요청 보내서 응답 받기
        AiTodayRecommendResponse response = callAi(0.0, userPrompt, AiTodayRecommendResponse.class);

        return response;
    }

    /**
     * 내일 날씨에 대한 옷 추천을 받는 메서드
     * @param request AI에게 넘겨줄 정보를 담은 Dto
     * @return 내일 날씨 기반 추천 옷(아우터, 상의, 하의 중 3개)
     */
    @Override
    public AiTomorrowRecommendResponse recommendTomorrow(AiRecommendRequest request) {
        // 1. 프롬프트 문자열 생성
        String userPrompt = promptTemplate.buildTomorrowRecommendPrompt(request);

        // 2. AI에게 요청 보내서 응답 받기
        AiTomorrowRecommendResponse response = callAi(0.7, userPrompt, AiTomorrowRecommendResponse.class);

        return response;
    }

    /**
     * 이번주 날씨에 대한 옷 추천을 받는 메서드
     * @param request AI에게 넘겨줄 정보를 담은 Dto
     * @return 이번주 날씨 기반 추천 옷(아우터, 상의, 하의 중 3개)
     */
    @Override
    public AiWeeklyRecommendResponse recommendWeekly(AiRecommendRequest request) {
        // 1. 프롬프트 문자열 생성
        String userPrompt = promptTemplate.buildWeeklyRecommendPrompt(request);

        // 2. AI에게 요청 보내서 응답 받기
        AiWeeklyRecommendResponse response = callAi(0.3, userPrompt, AiWeeklyRecommendResponse.class);

        return response;
    }

    /**
     * AI에게 요청 보낼 옵션을 생성하고 요청을 보내서 응답을 받는 메서드
     * @param temperature 온도 설정
     * @param userPrompt 사용자 프롬프트
     * @param responseType 응답 형식
     * @return AI에게 받은 응답
     * @param <T> 타입 템플릿(호출하는 쪽에서 결정)
     */
    private <T> T callAi(double temperature, String userPrompt, Class<T> responseType) {
        // 옵션 설정
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .temperature(temperature)
                .build();

        // 시스템 규칙 + 모델 파라미터로 호출
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userPrompt)
                .options(options)
                .call()
                .entity(responseType); // json타입으로 받는 설정(Jackson이 JSON → DTO 매핑)
    }
}
