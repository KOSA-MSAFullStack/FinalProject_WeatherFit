package com.fitcaster.weatherfit.review.api.dto.response;

import com.fitcaster.weatherfit.review.domain.entity.Review;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class ReviewResponse {

    private final String userName;
    private final BigDecimal ratingScore;
    private final String weather;
    private final String temperature;
    private final String indoorFit;
    private final String contents;
    private final LocalDate createdAt;

    // Review 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
            review.getUser().getName(), // User 객체에서 이름 가져오기
            review.getRatingScore(),
            review.getWeather().getDescription(), // Enum의 설명 반환
            review.getTemperature().getDescription(),
            review.getIndoorFit() != null ? review.getIndoorFit().getDescription() : null,
            review.getContents(),
            review.getCreatedAt()
        );
    }

    // 생성자
    private ReviewResponse(String userName, BigDecimal ratingScore, String weather,
                           String temperature, String indoorFit, String contents, LocalDate createdAt) {
        this.userName = userName;
        this.ratingScore = ratingScore;
        this.weather = weather;
        this.temperature = temperature;
        this.indoorFit = indoorFit;
        this.contents = contents;
        this.createdAt = createdAt;
    }
}