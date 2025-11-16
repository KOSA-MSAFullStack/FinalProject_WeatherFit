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
    // 예: {"아우터": ["패딩", "코트"], "상의": ["티셔츠", "니트"]}
    private Map<String, List<String>> categoryData;
}
