package com.fitcaster.weatherfit.recommendation.api.dto;

// 생성자 자동 생성
public record ItemBrief(
        Long itemId,                 // 상품 ID
        String category,             // 각 분류에 맞는 카테고리
        String itemName,             // 상품 이름
        int minTemperature,          // 최저 기온
        int maxTemperature,          // 최고 기온
        String gender,               // 성별
        String imgUrl                // 이미지 url
) {
}
