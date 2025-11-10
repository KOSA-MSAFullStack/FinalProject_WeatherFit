// ItemResponse.java
// 상품 목록 조회 응답 DTO

package com.fitcaster.weatherfit.catalog.api.dto;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
public class ItemResponse {
    private Long id;
    private String itemName;
    private String itemCode;
    private int price;
    private String gender;
    private String imageURL;
    private LocalDate createdAt;
    private String reviewAiSummary;

    // Item 엔티티로부터 DTO를 생성하는 팩토리 메서드
    public static ItemResponse from(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .itemCode(item.getItemCode())
                .price(item.getPrice())
                .gender(item.getGender())
                .imageURL(item.getImageURL())
                .createdAt(item.getCreatedAt())
                .reviewAiSummary(item.getReviewAiSummary())
                .build();
    }
}
