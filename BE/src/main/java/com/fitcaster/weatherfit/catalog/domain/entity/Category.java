// Category.java
// 상품 카테고리 엔티티

package com.fitcaster.weatherfit.catalog.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

// * author: 김기성
@Entity
@Table(name="CATEGORY")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category {
    // 카테고리 ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="category_id", nullable=false)
    private Long categoryId;

    // 분류 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="classification_id", nullable=false)
    private Classification classification;

    // 카테고리
    @Column(name="category", nullable=false, length=50)
    private String category;

    public Category(Classification classification, String category) {
        this.classification = classification;
        this.category = category;
    }
}
