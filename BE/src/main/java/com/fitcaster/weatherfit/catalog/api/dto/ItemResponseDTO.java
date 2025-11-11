// ItemResponseDTO.java
// 상품 목록 조회 응답 DTO

package com.fitcaster.weatherfit.catalog.api.dto;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

// * author: 김기성
@Getter
@Builder
public class ItemResponseDTO {
    private Long itemId;            // 상품 ID
    private String itemName;        // 상품명
    private String itemCode;        // 상품코드
    private int price;              // 가격
    private String gender;          // 성별 (남-M / 여-F / 남여공용-C)
    private String imageURL;        // 이미지 URL
    private String aiDescription;   // AI 설명
    private LocalDate createdAt;    // 상품 등록일
    private String reviewAiSummary; // AI가 요약한 리뷰

    // Item 엔티티로부터 DTO를 생성하는 메서드
    public static ItemResponseDTO from(Item item) {
        return ItemResponseDTO.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .itemCode(item.getItemCode())
                .price(item.getPrice())
                .gender(item.getGender())
                .imageURL(item.getImageURL())
                .aiDescription(item.getAiDescription())
                .createdAt(item.getCreatedAt())
                .reviewAiSummary(item.getReviewAiSummary())
                .build();
    }
}
