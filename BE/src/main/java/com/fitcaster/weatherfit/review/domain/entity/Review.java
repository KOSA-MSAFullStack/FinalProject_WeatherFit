package com.fitcaster.weatherfit.review.domain.entity;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "REVIEW")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private Long id;

    // FK: ITEM_ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private Item item;

    // FK: USER_ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "RATING_SCORE", nullable = false, precision = 3, scale = 1)
    private BigDecimal ratingScore;

    // Enum으로 관리 (DB에는 문자열로 저장)
    @Enumerated(EnumType.STRING)
    @Column(name = "WEATHER", nullable = false, length = 50)
    private Weather weather;

    @Enumerated(EnumType.STRING)
    @Column(name = "TEMPERATURE", nullable = false, length = 50)
    private Temperature temperature;

    @Enumerated(EnumType.STRING)
    @Column(name = "INDOOR_FIT", length = 50)
    private IndoorFit indoorFit;

    @Column(name = "CONTENTS", nullable = false, length = 1000)
    private String contents;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDate createdAt;

    // 수정 기능을 위한 메서드
    public void update(BigDecimal ratingScore, Weather weather, Temperature temperature,
                       IndoorFit indoorFit, String contents) {
        this.ratingScore = ratingScore;
        this.weather = weather;
        this.temperature = temperature;
        this.indoorFit = indoorFit;
        this.contents = contents;
    }

    @Builder
    public Review(Item item, User user, BigDecimal ratingScore, Weather weather,
                  Temperature temperature, IndoorFit indoorFit, String contents) {
        this.item = item;
        this.user = user;
        this.ratingScore = ratingScore;
        this.weather = weather;
        this.temperature = temperature;
        this.indoorFit = indoorFit;
        this.contents = contents;
        this.createdAt = LocalDate.now();
    }
}
