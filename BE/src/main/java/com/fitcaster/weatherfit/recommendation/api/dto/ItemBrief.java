package com.fitcaster.weatherfit.recommendation.api.dto;

/**
 * 1차 분류한 옷 정보에서 AI에게 넘겨줄 정보
 * @author 김경아
 */
public record ItemBrief(
        Long itemId,                 // 상품 ID
        String category,             // 각 분류에 맞는 카테고리
        String itemName,             // 상품 이름
        int minTemperature,          // 최저 기온
        int maxTemperature,          // 최고 기온
        String gender,               // 성별
        String imageURL              // 이미지URL
) {
}
