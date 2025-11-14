// AIService.java
// AI 관련 서비스

package com.fitcaster.weatherfit.catalog.ai.application;

import com.fitcaster.weatherfit.catalog.ai.api.dto.AIRequestDTO;
import com.fitcaster.weatherfit.common.exception.InternalServerException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import java.util.List;

// * author: 김기성
@Service
public class AIService {

    private final ChatClient chatClient;

    // ChatClient 주입 생성자
    public AIService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // 상품 정보/이미지 기반으로 AI 설명 생성
    public String generateDescription(AIRequestDTO request) {
        try {
            // 시스템 프롬프트 (AI 역할&출력 형식 정의)
            String systemPrompt = """
                    당신은 패션 전문가이자 상세한 상품 설명 작성가입니다.
                    제공되는 상품 정보와 이미지를 바탕으로, 사용자가 구매 결정을 내리는 데 도움이 될 만한 매력적인 상품 설명을 작성해야 합니다.
                    반드시 다음 5가지 항목을 포함하고, 각 항목의 제목 앞에는 이모지를 붙여주세요. 각 항목의 내용은 2~3문장으로 리스트화하여 작성해주세요.
                    결과는 마크다운 형식이나 다른 특별한 형식 없이, 일반 텍스트로만 제공해주세요.

                    - 📝 총평:
                    - 🌡️ 권장 기온대:
                    - ✨ 상황별 보완 팁:
                    - 👕 체질별 가이드:
                    - 👗 함께 코디하면 좋은 아이템:
                    """;

            // 상품 정보/이미지 기반으로 AI 설명 생성
            String productInfo = String.format(
                    "상품명: %s, 카테고리: %s, 성별: %s, 추천 계절: %s",
                    request.getItemName(),
                    request.getCategory(),
                    request.getGender(),
                    String.join(", ", request.getSeasonName() == null ? List.of() : request.getSeasonName())
            );

            // 이미지 리소스 추출 (람다)
            final Resource image =
                    request.getImage() != null ? request.getImage().getResource() : null;

            // ChatClient 호출  
            String result = chatClient.prompt()
                    .system(systemPrompt)
                    .user(u -> {
                        u.text("이 상품에 대한 설명을 생성해줘. 상품 정보: " + productInfo);
                        if (image != null) {
                            u.media(new MimeType("image","webp"), image);
                        }
                    })
                    .call()
                    .content();

            return result;

        } catch (Exception e) {
            throw new InternalServerException("⚠️ AI 설명 생성 중 오류 발생", e);
        }
    }
}
