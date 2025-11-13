//package com.fitcaster.weatherfit.recommendation.infra;
//
//import com.fitcaster.weatherfit.recommendation.api.dto.AiAllRecommendResponse;
//import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendRequest;
//import com.fitcaster.weatherfit.recommendation.application.port.AiPort;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.openai.OpenAiChatOptions;
//import org.springframework.stereotype.Component;
//
// 메인페이지용: 오늘/내일/이번주를 한 번에 추천
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class OpenAiAdapterAll implements AiPort {
//
//    private final ChatClient chatClient;
//    private final PromptTemplateAll promptTemplate; // 새로: all용 프롬프트도 여기서 생성
//
//    private static final String SYSTEM_PROMPT = """
//      당신은 날씨에 따른 옷을 추천해주는 쇼핑몰의 코디 추천 어시스턴트입니다.
//      불필요한 텍스트/설명/마크다운은 금지합니다.
//      JSON만 출력해야 하며, 스키마를 반드시 지켜야 합니다.
//    """;
//
//    private <T> T callAi(
//            String userPrompt,
//            double temperature,
//            Class<T> responseType
//    ) {
//        OpenAiChatOptions options = OpenAiChatOptions.builder()
//                .temperature(temperature)
//                .build();
//
//        return chatClient.prompt()
//                .system(SYSTEM_PROMPT)
//                .user(userPrompt)
//                .options(options)
//                .call()
//                .entity(responseType);
//    }
//
//    @Override
//    public AiAllRecommendResponse recommendAll(AiRecommendRequest request) {
//        String userPrompt = promptTemplate.buildAllRecommendPrompt(request);
//
//        AiAllRecommendResponse response = callAi(userPrompt, 0.5, AiAllRecommendResponse.class);
//
//        return response;
//    }
//}
