// Category.java
// [Entity] 상품 카테고리

package com.fitcaster.weatherfit.catalog.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name="CATEGORY")
@Getter
public class Category {
    // 카테고리 ID (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="category_id", nullable=false)
    private Long categoryId;

    // 분류 ID (FK)
    @ManyToOne
    @JoinColumn(name="classification_id", nullable=false)
    private Classification classification;

    // 카테고리
    @Column(name="category", nullable=false, length=50)
    private String category;
}
