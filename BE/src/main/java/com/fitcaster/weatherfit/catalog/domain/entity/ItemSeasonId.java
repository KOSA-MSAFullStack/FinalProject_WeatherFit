// ItemSeasonId.java
// ItemSeason 복합 키 클래스

package com.fitcaster.weatherfit.catalog.domain.entity;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// * author: 김기성
@NoArgsConstructor
@EqualsAndHashCode
public class ItemSeasonId implements Serializable {
    // 복합키 (상품, 계절)
    private Long item;
    private Long season;
}