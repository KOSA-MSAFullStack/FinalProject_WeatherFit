// ItemRequestDTO.java
// 상품 요청 DTO

package com.fitcaster.weatherfit.catalog.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// * author: 김기성
public class ItemRequestDTO {
    // 상품 등록 요청 DTO
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private Long categoryId;        // 카테고리 ID
        private String itemName;        // 상품명
        private String itemCode;        // 상품코드
        private int price;              // 가격
        private int quantity;           // 재고 수량
        private String gender;          // 성별 (남-M / 여-F / 남여공용-C)
        private String imageURL;        // 이미지 URL
        private String aiDescription;   // AI 설명
        private int maxTemperature;     // 최고기온
        private int minTemperature;     // 최저기온
    }
}