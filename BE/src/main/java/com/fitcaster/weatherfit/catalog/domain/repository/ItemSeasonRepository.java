package com.fitcaster.weatherfit.catalog.domain.repository;

import com.fitcaster.weatherfit.catalog.domain.entity.Classification;
import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.catalog.domain.entity.ItemSeason;
import com.fitcaster.weatherfit.catalog.domain.entity.ItemSeasonId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemSeasonRepository extends JpaRepository<ItemSeason, ItemSeasonId> {

    /**
     * 계절과 분류로 1차로 분류하기 위한 쿼리
     * @param classification 분류(아우터, 상의, 하의)
     * @param season 계절(봄, 여름, 가을, 겨울)
     * @return 분류와 계절에 맞게 필터링된 옷 리스트
     */
    @Query("""
            select i from ItemSeason is
            join is.item i
            join fetch i.category c
            join fetch c.classification cl
            where is.season.seasonName = :season AND
            cl.classification = :classification
            """
    )
    List<Item> findByClassificationAndSeason(@Param("classification") String classification, @Param("season") String season);

}
