package com.fitcaster.weatherfit.review.api.dto.response;

import com.fitcaster.weatherfit.review.domain.entity.IndoorFit;
import com.fitcaster.weatherfit.review.domain.entity.Review;
import com.fitcaster.weatherfit.review.domain.entity.Temperature;
import com.fitcaster.weatherfit.review.domain.entity.Weather;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * '리뷰 관리' 탭에서 내가 쓴 리뷰 목록을 조회하기 위한 응답 DTO
 */
@Getter
@NoArgsConstructor
public class UserReviewResponse {

    // --- 화면 표시에 직접적으로 사용되는 필드 ---

    private Long reviewId;      // 리뷰의 고유 ID (수정, 삭제 시 필요)
    private String itemName;      // 상품 이름 (예: "울 블렌드 인타르시아 니트 탑")
    private LocalDate createdAt;    // 리뷰 작성일 (예: "2025.10.29")
    private BigDecimal ratingScore; // 별점 (예: 4.5)
    private String contents;      // 리뷰 내용

    // --- '수정' 버튼 클릭 시 모달에 기존 값을 채우기 위한 필드 ---

    private Long itemId;          // 리뷰 대상 상품의 ID
    private Weather weather;         // 착용일 날씨 (Enum: SUNNY, CLOUDY 등)
    private Temperature temperature;   // 날씨 체감 (Enum: WARM, COLD 등)
    private IndoorFit indoorFit;     // 실내 착용감 (Enum: COMFORTABLE 등)


    /**
     * @param review 변환할 Review 엔티티 객체
     * @return 변환된 DTO 객체
     */
    public static UserReviewResponse from(Review review) {
        UserReviewResponse dto = new UserReviewResponse();

        // 화면 표시용 데이터 매핑
        dto.reviewId = review.getId();
        dto.itemName = review.getItem().getItemName();
        dto.createdAt = review.getCreatedAt();
        dto.ratingScore = review.getRatingScore();
        dto.contents = review.getContents();

        // 수정 기능용 데이터 매핑
        dto.itemId = review.getItem().getItemId();
        dto.weather = review.getWeather();
        dto.temperature = review.getTemperature();
        dto.indoorFit = review.getIndoorFit();

        return dto;
    }
}
