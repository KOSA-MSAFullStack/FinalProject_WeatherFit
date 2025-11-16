// CategoryResponseDTO.java
// 카테고리 조회 응답 DTO

package com.fitcaster.weatherfit.catalog.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

// * author: 김기성
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    // 분류별 카테고리 목록
    // 예: {"아우터": [{"categoryId": 1, "category": "패딩"}, ...], "상의": [...]}
    private Map<String, List<CategoryInfo>> categoryData;
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private Long categoryId;
        private String category;
    }
}
