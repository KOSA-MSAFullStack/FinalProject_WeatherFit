// ItemRequestDTO.java
// 상품 요청 DTO

package com.fitcaster.weatherfit.catalog.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class ItemRequestDTO {
    // 상품 등록 요청 DTO
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private Long categoryId;        // 상품 ID
        private String itemName;        // 상품명
        private String itemCode;        // 상품코드
        private int price;              // 가격
        private int quantity;           // 재고 수량
        private int minTemperature;     // 최저기온
        private int maxTemperature;     // 최고기온
        private String gender;          // 성별
        private String imageURL;        // 이미지 URL
        private String aiDescription;   // AI 설명
    }
}