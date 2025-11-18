package com.fitcaster.weatherfit.review.api.dto.response;

import com.fitcaster.weatherfit.review.domain.entity.IndoorFit;
import com.fitcaster.weatherfit.review.domain.entity.Review;
import com.fitcaster.weatherfit.review.domain.entity.Temperature;
import com.fitcaster.weatherfit.review.domain.entity.Weather;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class ReviewResponse {

    private final String userName;
    private final BigDecimal ratingScore;
    private final Weather weather;
    private final Temperature temperature;
    private final IndoorFit indoorFit;
    private final String contents;
    private final LocalDate createdAt;

    // Review 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
            review.getUser().getName(), // User 객체에서 이름 가져오기
            review.getRatingScore(),
            review.getWeather(),
            review.getTemperature(),
            review.getIndoorFit(),
            review.getContents(),
            review.getCreatedAt()
        );
    }

    // 생성자
    private ReviewResponse(String userName, BigDecimal ratingScore, Weather weather,
                           Temperature temperature, IndoorFit indoorFit, String contents, LocalDate createdAt) {
        this.userName = userName;
        this.ratingScore = ratingScore;
        this.weather = weather;
        this.temperature = temperature;
        this.indoorFit = indoorFit;
        this.contents = contents;
        this.createdAt = createdAt;
    }
}