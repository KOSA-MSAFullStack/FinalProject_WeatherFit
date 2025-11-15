// AIResponseDTO.java
// AI 설명 생성 응답 DTO

package com.fitcaster.weatherfit.catalog.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// * author: 김기성
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AIResponseDTO {
    private String content;     // 내용
    private Integer minTemperature; // 최저 기온
    private Integer maxTemperature; // 최고 기온
}
