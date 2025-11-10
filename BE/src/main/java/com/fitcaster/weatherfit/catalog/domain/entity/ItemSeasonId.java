// ItemSeasonId.java
// ItemSeason 복합 키 클래스

package com.fitcaster.weatherfit.catalog.domain.entity;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
public class ItemSeasonId implements Serializable {
    private Long item;
    private Long season;
}