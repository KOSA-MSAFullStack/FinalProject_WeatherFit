package com.fitcaster.weatherfit.recommendation.infra;

import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendRequest;
import com.fitcaster.weatherfit.recommendation.api.dto.ItemBrief;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 요청으로 보낼 user prompt를 생성하는 클래스
 * @author 김경아
 */
@Component
public class PromptTemplate {

    /**
     * 오늘 날씨 정보와 분류와 계절로 1차분류한 상품 목록(아우터, 상의, 하의)를 받아 LLM에 보낼 사용자 프롬프트 문자열을 생성하는 메서드
     * @param request  날씨정보, 아우터 목록, 상의 목록, 하의 목록이 담긴 DTO
     * @return LLM에 그대로 전달 가능한 완성된 프롬프트 문자열
     */
    public String buildTodayRecommendPrompt(AiRecommendRequest request) {
        return """
        [요구사항]
        - 오늘 날씨(최저 %.1f°C, 최고 %.1f°C, 상태: %s)에 맞춰 outer 1개, top 1개, bottom 1개를 추천하세요.
        - 상품의 최저기온과 최고기온을 파악해서 오늘의 날씨에 최적화된 옷을 추천해주세요.
        - 반드시 아래 JSON "정확한 스키마"로만 출력(코드블록 금지, 설명 금지).
        {
            "outer": { "itemId": <Long>,"itemName": "<String>", "imgUrl": "<String>" },
            "top":   { "itemId": <Long>,"itemName": "<String>", "imgUrl": "<String>" },
            "bottom":{ "itemId": <Long>,"itemName": "<String>", "imgUrl": "<String>" }
          }
        - 선택은 아래 후보 목록에서만 하며, 선택한 항목의 필드를 그대로 복사하여 채우세요(값 생성/변경 금지).
        - 숫자 필드는 따옴표 없이 숫자로 출력.

        [날씨]
        - 날짜: %s
        - 최저/최고: %.1f/%.1f
        - 상태: %s

        [후보: OUTER]
        %s

        [후보: TOP]
        %s

        [후보: BOTTOM]
        %s
        """.formatted(
                request.getWeather().getMinTemperature(), request.getWeather().getMaxTemperature(), request.getWeather().getCondition(),
                request.getWeather().getDate(), request.getWeather().getMinTemperature(), request.getWeather().getMaxTemperature(), request.getWeather().getCondition(),
                toLines(request.getOuters()), toLines(request.getTops()), toLines(request.getBottoms())
        );
    }

    /**
     * 내일 날씨 정보와 분류와 계절로 1차분류한 상품 목록(아우터, 상의, 하의)를 받아 LLM에 보낼 사용자 프롬프트 문자열을 생성하는 메서드
     * @param request  날씨정보, 아우터 목록, 상의 목록, 하의 목록이 담긴 DTO
     * @return LLM에 그대로 전달 가능한 완성된 프롬프트 문자열
     */
    public String buildTomorrowRecommendPrompt(AiRecommendRequest request) {
        return """
        [요구사항]
        - 내일 날씨(최저 %.1f°C, 최고 %.1f°C, 상태: %s)에 맞춰 12개의 옷을 추리고 그 중에서 랜덤으로 3개의 옷을 추천하세요.
        - 오늘 추천과 똑같은 조합이 나오지 않도록 "다소 다른 스타일"을 선택하세요.
        - 최적값 1개만 고르지 말고 유사한 후보 중 다른 스타일을 우선적으로 선택하세요.
        - minTemperature와 maxTemperature가 ‘대략적으로’ 맞는 옷 중에서
          오늘 추천에서 선택될 가능성이 높은 아이템은 피하고,
          다른 대안 후보를 선택하세요.
        - 최적값보다는 다양하게 선택하는 것을 우선하세요.
        - 반드시 아래 JSON "정확한 스키마"로만 출력(코드블록 금지, 설명 금지).
          {
            "firstItem": { "itemId": <Long>,"itemName": "<String>", "imgUrl": "<String>" },
            "secondItem": { "itemId": <Long>,"itemName": "<String>", "imgUrl": "<String>" },
            "thirdItem":{ "itemId": <Long>,"itemName": "<String>", "imgUrl": "<String>" }
          }
        - 선택은 아래 후보 목록에서만 하며, 선택한 항목의 필드를 그대로 복사하여 채우세요(값 생성/변경 금지).
        - 숫자 필드는 따옴표 없이 숫자로 출력.

        [날씨]
        - 날짜: %s
        - 최저/최고: %.1f/%.1f
        - 상태: %s

        [후보]
        %s
        %s
        %s
        """.formatted(
                request.getWeather().getMinTemperature(), request.getWeather().getMaxTemperature(), request.getWeather().getCondition(),
                request.getWeather().getDate(), request.getWeather().getMinTemperature(), request.getWeather().getMaxTemperature(), request.getWeather().getCondition(),
                toLines(request.getOuters()), toLines(request.getTops()), toLines(request.getBottoms())
        );
    }

    /**
     * LLM에 제시할 후보 아이템 목록을 한 줄 문자열로 변환하는 메서드
     * @param items 프롬프트에 나열할 후보 아이템 목록
     * @return 단일 문자열 (예: "id=8 | 코트 | 울 블렌드 발마칸 코트 | 5 | 10 | 여)
     */
    private String toLines(List<ItemBrief> items) {
        return items.stream()
                .map(item -> "id=%d | %s | %s | %d | %d | %s"
                        .formatted(
                item.itemId(), item.category(), item.itemName(), item.minTemperature(), item.maxTemperature(), item.gender()))
                .collect(Collectors.joining("\n"));
    }
}

