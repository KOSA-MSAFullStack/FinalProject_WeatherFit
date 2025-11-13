//package com.fitcaster.weatherfit.recommendation.infra;
//
//import com.fitcaster.weatherfit.recommendation.api.dto.AiRecommendRequest;
//import com.fitcaster.weatherfit.recommendation.api.dto.ItemBrief;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
// 메인페이지용: 오늘/내일/이번주를 한 번에 추천
//@Component
//public class PromptTemplateAll {
//
//    public String buildAllRecommendPrompt(AiRecommendRequest request) {
//
//        // 편의를 위해 가정:
//        // request.getTodayWeather(), getTomorrowWeather(), getWeeklyWeather()
//        // request.getOuters(), getTops(), getBottoms() 같은 메서드가 있다고 치겠습니다.
//
//        var today = request.getTodayWeather();
//        var tomorrow = request.getTomorrowWeather();
//        var weekly = request.getWeeklyWeather(); // 대표값
//
//        String outerLines = toLines(request.getOuters());
//        String topLines   = toLines(request.getTops());
//        String bottomLines= toLines(request.getBottoms());
//
//        return """
//        [역할]
//        - 당신은 날씨 기반 옷차림 추천 쇼핑몰의 코디 추천 어시스턴트입니다.
//        - 오늘 / 내일 / 이번주에 대해 서로 다른 아이템 조합을 추천해야 합니다.
//
//        [전역 규칙]
//        - 전체 응답(today, tomorrow, weekly)을 통틀어 itemId가 중복되면 안 됩니다.
//        - 한 번 사용한 itemId는 다시 사용할 수 없습니다.
//        - JSON 이외의 텍스트(설명, 마크다운, 말)는 절대 출력하지 마세요.
//        - 스키마를 반드시 지키고, 값은 후보 목록에서 그대로 복사하세요(값 생성/변경 금지).
//
//        [출력 스키마]
//        {
//          "today": {
//            "outer":  { "itemId": <Long>, "itemName": "<String>", "imgUrl": "<String>" },
//            "top":    { "itemId": <Long>, "itemName": "<String>", "imgUrl": "<String>" },
//            "bottom": { "itemId": <Long>, "itemName": "<String>", "imgUrl": "<String>" }
//          },
//          "tomorrow": {
//            "firstItem":  { "itemId": <Long>, "itemName": "<String>", "imgUrl": "<String>" },
//            "secondItem": { "itemId": <Long>, "itemName": "<String>", "imgUrl": "<String>" },
//            "thirdItem":  { "itemId": <Long>, "itemName": "<String>", "imgUrl": "<String>" }
//          },
//          "weekly": {
//            "firstItem":  { "itemId": <Long>, "itemName": "<String>", "imgUrl": "<String>" },
//            "secondItem": { "itemId": <Long>, "itemName": "<String>", "imgUrl": "<String>" },
//            "thirdItem":  { "itemId": <Long>, "itemName": "<String>", "imgUrl": "<String>" }
//          }
//        }
//
//        [요구사항 - TODAY]
//        - 오늘 날씨(최저 %.1f°C, 최고 %.1f°C, 상태: %s)에 최적화된 outer/top/bottom 을 1개씩 추천하세요.
//        - 기온과 날씨에 가장 잘 맞는 조합을 선택하세요.
//
//        [요구사항 - TOMORROW]
//        - 내일 날씨(최저 %.1f°C, 최고 %.1f°C, 상태: %s)에 맞춰 "서로 다른" 3개의 단일 아이템을 추천하세요.
//        - 오늘과 동일한 아이템이 선택되지 않도록, 다른 스타일을 우선적으로 선택하세요.
//
//        [요구사항 - WEEKLY]
//        - 이번주 대표 날씨(최저 %.1f°C, 최고 %.1f°C, 상태: %s)에 맞는 3개의 단일 아이템을 추천하세요.
//        - 오늘/내일 추천과 중복되지 않는 범위에서, 이번주 전체에 무난하게 입기 좋은 아이템을 선택하세요.
//
//        [날씨 정보]
//        - 오늘:   날짜=%s, 최저/최고=%.1f/%.1f, 상태=%s
//        - 내일:   날짜=%s, 최저/최고=%.1f/%.1f, 상태=%s
//        - 이번주: 대표값 최저/최고=%.1f/%.1f, 상태=%s
//
//        [후보: OUTER]
//        %s
//
//        [후보: TOP]
//        %s
//
//        [후보: BOTTOM]
//        %s
//        """.formatted(
//                // TODAY 요약
//                today.getMinTemperature(), today.getMaxTemperature(), today.getCondition(),
//                // TOMORROW 요약
//                tomorrow.getMinTemperature(), tomorrow.getMaxTemperature(), tomorrow.getCondition(),
//                // WEEKLY 요약
//                weekly.getMinTemperature(), weekly.getMaxTemperature(), weekly.getCondition(),
//
//                // 날씨 상세
//                today.getDate(), today.getMinTemperature(), today.getMaxTemperature(), today.getCondition(),
//                tomorrow.getDate(), tomorrow.getMinTemperature(), tomorrow.getMaxTemperature(), tomorrow.getCondition(),
//                weekly.getMinTemperature(), weekly.getMaxTemperature(), weekly.getCondition(),
//
//                // 후보 목록
//                outerLines,
//                topLines,
//                bottomLines
//        );
//    }
//
//    private String toLines(List<ItemBrief> items) {
//        return items.stream()
//                .map(item -> "id=%d | %s | %s | %d | %d | %s"
//                        .formatted(
//                                item.itemId(), item.category(), item.itemName(), item.minTemperature(), item.maxTemperature(), item.gender()))
//                .collect(Collectors.joining("\n"));
//    }
//}
//
