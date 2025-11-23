// ReviewAIService.java
// [AI 리뷰 요약 서비스]

package com.fitcaster.weatherfit.review.ai.application;

import com.fitcaster.weatherfit.common.exception.InternalServerException;
import com.fitcaster.weatherfit.review.domain.entity.Review;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

// * author: 김기성
@Service
public class ReviewAIService {

    private final ChatClient chatClient;

    public ReviewAIService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // 리뷰 목록 기반으로 AI 리뷰 요약 생성
    public String summarizeReviews(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return "아직 작성된 리뷰가 없습니다.";
        }

        try {
            String systemPrompt = """
                [역할]
                - 당신은 '웨더핏(WeatherFit)'의 쇼핑 어시스턴트입니다.
                - 당신의 임무는 사용자들이 작성한 리뷰를 전부 분석하고, 핵심 내용을 요약하여 다른 구매자들에게 상품에 대한 유용한 정보를 제공하는 것입니다.

                [작업 목표]
                - 주어진 리뷰 목록을 바탕으로, 긍정적인 점과 아쉬운 점을 간결하게 요약합니다.
                - 구매자들이 가장 궁금해할 만한 내용(예: 사이즈, 핏, 착용감, 날씨 적합성 등)을 중심으로 핵심만 추출합니다.

                [작성 언어/톤]
                - 반드시 한국어로만 작성합니다.
                - 영어 또는 한자 등의 다른 언어는 사용하지 않고 작성합니다.
                - \"~해요\", \"~입니다\"와 같이 부드럽고 친절한 어투를 사용합니다.
                - AI 모델이라는 사실은 절대 언급하지 않고, 마치 동료 쇼핑객이나 매장 직원이 설명해주는 것처럼 자연스럽게 작성합니다.
                - "사이즈·계절감·배송 등에 대한 구체적인 언급은 없었어요." 등과 같은 언급은 절대 하지 않도록 합니다.
                - "리뷰에 따르면," 등과 같은 불필요한 언급은 하지 않도록 합니다.

                [출력 형식]
                - 다른 설명 없이, 최종 요약 결과물만 "일반 텍스트"로 제공합니다.
                - 2~4개의 짧은 문장으로 구성된 간결한 단락으로 요약합니다.
                - 마크다운, HTML 등 다른 형식은 사용하지 않습니다.

                [출력 예시]
                - \"많은 분들이 예상보다 사이즈가 넉넉하게 나왔다고 하니 참고하세요. 특히 간절기에 입기 좋은 두께감과 부드러운 착용감에 만족도가 높아요.\"
                - \"두께감이 생각보다 있어 더웠다는 후기가 있어요. 대신 착용감은 편하고 부드럽다는 평가입니다. 전반적으로 추운 날씨에 더 잘 맞고, 더운 계절엔 답답할 수 있어요.\"
                """;

            String reviewContents = reviews.stream()
                    .map(Review::getContents)
                    .collect(Collectors.joining("\n- "));

            // OpenAI 옵션 설정
            OpenAiChatOptions options = OpenAiChatOptions.builder()
                    .model("gpt-4o-mini")             // OpenAI API 모델 설정
                    .temperature(0.5)     // 1.0일수록 창의적, but 느리고 일관성 떨어짐
                    .build();

            return chatClient.prompt()
                    .system(systemPrompt)
                    .user("다음 리뷰들을 요약해줘:\n- " + reviewContents)
                    .options(options)       // OpenAI 옵션 설정
                    .call()
                    .content();

        } catch (Exception e) {
            throw new InternalServerException("⚠️ AI 리뷰 요약 생성 중 오류가 발생했습니다.", e);
        }
    }
}
