// ItemSeason.java
// [Entity] 상품과 계절의 관계 조인 테이블

package com.fitcaster.weatherfit.catalog.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "ITEM_SEASON")
@Getter
@IdClass(ItemSeasonId.class)
public class ItemSeason {
    // 상품 ID (FK)
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 계절 ID (FK)
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    private Season season;
}
