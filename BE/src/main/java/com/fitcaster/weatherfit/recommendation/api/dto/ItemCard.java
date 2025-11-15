package com.fitcaster.weatherfit.recommendation.api.dto;

/**
 * AI가 추천해준 옷에 대한 정보
 * @author 김경아
 */
public record ItemCard(
        Long itemId,                 // 상품 ID
        String itemName,             // 상품 이름
        String imageURL                // 이미지 url
) {
}
