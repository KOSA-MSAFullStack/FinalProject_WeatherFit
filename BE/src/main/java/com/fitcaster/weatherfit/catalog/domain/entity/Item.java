// Item.java
// 상품 엔티티

package com.fitcaster.weatherfit.catalog.domain.entity;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

// * author: 김기성
@Entity
@Table(name = "ITEM")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Item {
    // 상품 ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="item_id", nullable = false)
    private Long itemId;

    // 카테고리 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id", nullable=false)
    private Category category;

    // 상품명
    @Column(name="item_name", nullable=false, length=255)
    private String itemName;

    // 상품 코드
    @Column(name="item_code", nullable=false, length=255)
    private String itemCode;

    // 가격
    @Column(name="price", nullable=false)
    private int price;

    // 재고 수량
    @Column(name="quantity", nullable=false)
    private int quantity;

    // 성별 (남-M / 여-F / 남여공용-C)
    @Column(name="gender", nullable=false, length=1)
    private String gender;

    // 이미지 URL
    @Column(name="image_url", nullable=false, length=255)
    private String imageURL;

    // AI 설명
    @Lob
    @Column(name="ai_description", nullable=false)
    private String aiDescription;

    // 최고 기온
    @Column(name="max_temperature", nullable=false)
    private int maxTemperature;

    // 최저 기온
    @Column(name="min_temperature", nullable=false)
    private int minTemperature;

    // 상품 등록일
    @Column(name="created_at", nullable=false)
    private LocalDate createdAt;

    // AI가 요약한 리뷰
    @Column(name="review_ai_summary", nullable=true, length=255)
    private String reviewAiSummary;

    // 상품-계절 연관 관계
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ItemSeason> itemSeasons = new ArrayList<>();



    // == 비즈니스 로직 == //
    // 카테고리 업데이트
    public void updateCategory(Category category) {
        this.category = category;
    }

    // 상품 상세 정보 업데이트 (부분 업데이트 지원)
    public void updateDetails(String itemName, Integer price, Integer quantity, String gender,
                              String imageURL, String aiDescription, Integer maxTemperature, Integer minTemperature) {
        if (itemName != null && !itemName.isEmpty()) {
            this.itemName = itemName;
        }
        if (price != null && price >= 0) {
            this.price = price;
        }
        if (quantity != null && quantity >= 0) {
            this.quantity = quantity;
        }
        if (gender != null && !gender.isEmpty()) {
            this.gender = gender;
        }
        if (imageURL != null && !imageURL.isEmpty()) {
            this.imageURL = imageURL;
        }
        if (aiDescription != null && !aiDescription.isEmpty()) {
            this.aiDescription = aiDescription;
        }
        if (maxTemperature != null) {
            this.maxTemperature = maxTemperature;
        }
        if (minTemperature != null) {
            this.minTemperature = minTemperature;
        }
    }
}
