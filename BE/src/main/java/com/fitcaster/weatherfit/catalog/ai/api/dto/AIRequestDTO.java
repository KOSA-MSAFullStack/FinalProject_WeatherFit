// AIRequestDTO.java
// AI 설명 생성 요청 DTO

package com.fitcaster.weatherfit.catalog.ai.api.dto;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// * author: 김기성
@Getter
@Setter
@ToString
public class AIRequestDTO {
    private String itemName;        // 상품명
    private String category;        // 카테고리
    private String gender;          // 성별
    private List<String> seasonName;   // 계절
    private MultipartFile image;    // 상품 이미지
}
