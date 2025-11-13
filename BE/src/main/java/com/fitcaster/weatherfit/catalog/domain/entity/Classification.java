// Classification.java
// 상품 분류 엔티티

package com.fitcaster.weatherfit.catalog.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

// * author: 김기성
@Entity
@Table(name="CLASSIFICATION")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Classification {
    // 분류 ID (PK)
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="classification_id", nullable=false)
    private Long classificationId;

    // 분류 (아우터 / 상의 / 하의)
    @Column(name="classification", nullable=false, length=50)
    private String classification;

    public Classification(String classification) {
        this.classification = classification;
    }
}
