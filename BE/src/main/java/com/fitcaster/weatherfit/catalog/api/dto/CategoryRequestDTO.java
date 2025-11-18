// CategoryRequestDTO.java
// 카테고리 요청 DTO

package com.fitcaster.weatherfit.catalog.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// * author: 김기성
public class CategoryRequestDTO {
    
    // 카테고리 추가 요청 DTO
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private String classification; // 분류명 (아우터, 상의, 하의)
        private String category;       // 카테고리명
    }
    
    // 카테고리 수정 요청 DTO
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        private String newCategoryName; // 새 카테고리명
    }
}
