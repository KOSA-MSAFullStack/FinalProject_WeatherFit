package com.fitcaster.weatherfit.recommendation.infra;

import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendRequest;
import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendResponse;
import com.fitcaster.weatherfit.recommendation.application.port.AiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

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


    @Override
    public AiRecommendResponse recommendToday(AiRecommendRequest request) {
        // 1. 프롬프트 문자열 생성
        String userPrompt = promptTemplate.buildPrompt(request);

        // 2. 시스템 규칙 + 모델 파라미터로 호출
        AiRecommendResponse response = chatClient.prompt()
                .system("SYSTEM_PROMPT")
                .user(userPrompt)
                .call()
                .entity(AiRecommendResponse.class); // json타입으로 받는 설정(Jackson이 JSON → DTO 매핑)

        return response;
    }

}
