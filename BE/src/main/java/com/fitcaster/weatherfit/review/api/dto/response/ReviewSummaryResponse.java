package com.fitcaster.weatherfit.review.api.dto.response;

import com.fitcaster.weatherfit.review.domain.entity.IndoorFit;
import com.fitcaster.weatherfit.review.domain.entity.Temperature;
import com.fitcaster.weatherfit.review.domain.entity.Weather;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Map;

@Getter
public class ReviewSummaryResponse {

    private final long totalReviews;
    private final BigDecimal averageRating;
    private final Map<Weather, Long> weatherStatistics;
    private final Map<Temperature, Long> temperatureStatistics;
    private final Map<IndoorFit, Long> indoorFitStatistics;

    private final Page<ReviewResponse> reviews;

    public ReviewSummaryResponse(long totalReviews, BigDecimal averageRating,
                                 Map<Weather, Long> weatherStatistics, Map<Temperature, Long> temperatureStatistics,
                                 Map<IndoorFit, Long> indoorFitStatistics, Page<ReviewResponse> reviews) {
        this.totalReviews = totalReviews;
        this.averageRating = averageRating;
        this.weatherStatistics = weatherStatistics;
        this.temperatureStatistics = temperatureStatistics;
        this.indoorFitStatistics = indoorFitStatistics;
        this.reviews = reviews;
    }
}
