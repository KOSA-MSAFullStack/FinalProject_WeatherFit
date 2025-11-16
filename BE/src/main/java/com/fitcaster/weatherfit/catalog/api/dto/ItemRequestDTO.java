// ItemRequestDTO.java
// 상품 요청 DTO

package com.fitcaster.weatherfit.catalog.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

// * author: 김기성
public class ItemRequestDTO {
    // 상품 등록 요청 DTO
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private String itemName;        // 상품명
        private String itemCode;        // 상품 코드
        private int price;              // 가격
        private int quantity;           // 재고 수량
        private String gender;          // 성별 (남-M / 여-F / 남여공용-C)
        private String aiDescription;   // AI 설명
        private Integer maxTemperature; // 최고기온
        private Integer minTemperature; // 최저기온
        private String category;        // 카테고리 이름
        private List<String> seasonName;   // 계절 목록
        private MultipartFile image;    // 이미지 파일
    }
    
    // 상품 수정 요청 DTO
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        private String itemName;        // 상품명
        private String itemCode;        // 상품코드
        private int price;              // 가격
        private int quantity;           // 재고 수량
        private String gender;          // 성별 (남-M / 여-F / 남여공용-C)
        private String aiDescription;   // AI 설명
        private Integer maxTemperature; // 최고기온
        private Integer minTemperature; // 최저기온
        private String category;        // 카테고리 이름
        private List<String> seasonName;   // 계절 목록
        private MultipartFile image;    // 이미지 파일
    }
}