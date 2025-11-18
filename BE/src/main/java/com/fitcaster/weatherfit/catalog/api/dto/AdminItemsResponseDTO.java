// AdminItemsResponse.java
// 관리자 페이지 상품 목록 응답 DTO

package com.fitcaster.weatherfit.catalog.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

// * author: 김기성
@Getter
@AllArgsConstructor
public class AdminItemsResponseDTO {
    private Page<ItemResponseDTO> items;
    private long sellingCount;      // 판매중
    private long soldOutCount;      // 품절
}
