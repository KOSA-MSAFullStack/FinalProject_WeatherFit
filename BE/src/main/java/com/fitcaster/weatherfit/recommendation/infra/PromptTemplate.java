package com.fitcaster.weatherfit.recommendation.infra;

import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendRequest;
import com.fitcaster.weatherfit.recommendation.api.dto.ItemBrief;
import com.fitcaster.weatherfit.weather.api.dto.WeatherResponse;
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
        WeatherResponse weather = request.getWeather(); // 날씨 정보

        return """
        [요구사항]
        - 오늘 날씨(최저 %.1f°C, 최고 %.1f°C, 상태: %s)에 맞춰 outer 1개, top 1개, bottom 1개를 추천하세요.
        - 상품의 최저기온과 최고기온을 파악해서 오늘의 날씨에 최적화된 옷을 추천해주세요.
        - 반드시 아래 JSON "정확한 스키마"로만 출력(코드블록 금지, 설명 금지).
        {
            "outer": { "itemId": <Long>,"itemName": "<String>", "imageURL": "<String>" },
            "top":   { "itemId": <Long>,"itemName": "<String>", "imageURL": "<String>" },
            "bottom":{ "itemId": <Long>,"itemName": "<String>", "imageURL": "<String>" }
          }
        - 선택은 아래 후보 목록에서만 하며, 선택한 항목의 필드를 그대로 복사하여 채우세요. 절대 후보에 없는 데이터를 생성하지 마세요.(값 생성/변경 금지).
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
                weather.getMinTemperature(), weather.getMaxTemperature(), weather.getCondition(),
                weather.getDate(), weather.getMinTemperature(), weather.getMaxTemperature(), weather.getCondition(),
                toLines(request.getOuters()), toLines(request.getTops()), toLines(request.getBottoms())
        );
    }

    /**
     * 내일 날씨 정보와 분류와 계절로 1차분류한 상품 목록(아우터, 상의, 하의)를 받아 LLM에 보낼 사용자 프롬프트 문자열을 생성하는 메서드
     * @param request  날씨정보, 아우터 목록, 상의 목록, 하의 목록이 담긴 DTO
     * @return LLM에 그대로 전달 가능한 완성된 프롬프트 문자열
     */
    public String buildTomorrowRecommendPrompt(AiRecommendRequest request) {
        WeatherResponse weather = request.getWeather(); // 날씨 정보

        return """
        [요구사항]
        - 내일 날씨(최저 %.1f°C, 최고 %.1f°C, 상태: %s)에 맞춰 12개의 옷을 추리고 그 중에서 랜덤으로 3개의 옷을 추천하세요.

        - 반드시 아래 JSON "정확한 스키마"로만 출력(코드블록 금지, 설명 금지).
          {
            "firstItem": { "itemId": <Long>,"itemName": "<String>", "imageURL": "<String>" },
            "secondItem": { "itemId": <Long>,"itemName": "<String>", "imageURL": "<String>" },
            "thirdItem":{ "itemId": <Long>,"itemName": "<String>", "imageURL": "<String>" }
          }
        - 선택은 아래 후보 목록에서만 하며, 선택한 항목의 필드를 그대로 복사하여 채우세요. 절대 후보에 없는 데이터를 생성하지 마세요.(값 생성/변경 금지).
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
                weather.getMinTemperature(), weather.getMaxTemperature(), weather.getCondition(),
                weather.getDate(), weather.getMinTemperature(), weather.getMaxTemperature(), weather.getCondition(),
                toLines(request.getOuters()), toLines(request.getTops()), toLines(request.getBottoms())
        );
    }

    /**
     * 이번주 날씨 정보와 분류와 계절로 1차분류한 상품 목록(아우터, 상의, 하의)를 받아 LLM에 보낼 사용자 프롬프트 문자열을 생성하는 메서드
     * @param request  날씨정보, 옷 목록이 담긴 DTO
     * @return LLM에 그대로 전달 가능한 완성된 프롬프트 문자열
     */
    public String buildWeeklyRecommendPrompt(AiRecommendRequest request) {
        WeatherResponse weather = request.getWeather(); // 날씨 정보

        return """
        [요구사항]
        - 이번주 대표 날씨(최저 %.1f°C, 최고 %.1f°C, 상태: %s)에 맞춰 총 3개의 상품을 추천하세요.
        - 카테고리는 상관없으며, 이번주 전체에 무난하게 입기 좋은 아이템을 선택하세요.
        - 아우터/상의/하의 구분 없이 아래 전체 후보 목록에서 서로 다른 3개를 선택하세요.
        - 이번주 내내 무난하게 입기 좋은 상품을 우선적으로 선택하되, 서로 다른 스타일이 되도록 해주세요.
        - 사용자의 성별과 각 아이템의 성별 속성을 반드시 고려해서 실제로 착용 가능한 조합만 선택하세요.
        - 아이템의 gender 값은 "M", "F", "C" 중 하나입니다.
        - 사용자의 성별이 "1"인 경우 gender가 "F" 또는 "C"인 상품만 선택하세요.
          사용자의 성별이 "0"인 경우 gender가 "M" 또는 "C"인 상품만 선택하세요.
        - 반드시 아래 JSON 스키마를 정확히 지키고, 코드블록이나 설명은 출력하지 마세요.
        {
          "firstItem": { "itemId": <Long>,"itemName": "<String>", "imageURL": "<String>" },
          "secondItem": { "itemId": <Long>,"itemName": "<String>", "imageURL": "<String>" },
          "thirdItem":{ "itemId": <Long>,"itemName": "<String>", "imageURL": "<String>" }
        }
        - 선택은 아래 후보 목록에서만 하며, 선택한 항목의 필드를 그대로 복사해서 채우세요. 절대 후보에 없는 데이터를 생성하지 마세요.(값 생성, 변경 금지)
        - 숫자는 따옴표 없이 숫자로 출력합니다.

        [날씨]
        - 날짜: %s
        - 최저/최고: %.1f/%.1f
        - 상태: %s

        [후보]
        %s
        %s
        %s
        """.formatted(
                weather.getMinTemperature(), weather.getMaxTemperature(), weather.getCondition(),
                weather.getDate(), weather.getMinTemperature(), weather.getMaxTemperature(), weather.getCondition(),
                toLines(request.getOuters()), toLines(request.getTops()), toLines(request.getBottoms())
//                request.getOuters(), request.getTops(), request.getBottoms()
        );
    }

    /**
     * LLM에 제시할 후보 아이템 목록을 한 줄 문자열로 변환하는 메서드
     * @param items 프롬프트에 나열할 후보 아이템 목록
     * @return 단일 문자열 (예: "id=8 | 코트 | 울 블렌드 발마칸 코트 | 5 | 10 | 여성)
     */
    private String toLines(List<ItemBrief> items) {
        return items.stream()
                .map(item -> "id=%d | %s | %s | %d | %d | %s | %s"
                        .formatted(
                item.itemId(), item.category(), item.itemName(), item.minTemperature(), item.maxTemperature(), item.gender(), item.imageURL()))
                .collect(Collectors.joining("\n"));
    }
}

