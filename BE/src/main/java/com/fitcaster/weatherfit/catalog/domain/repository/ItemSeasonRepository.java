package com.fitcaster.weatherfit.catalog.domain.repository;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.catalog.domain.entity.ItemSeason;
import com.fitcaster.weatherfit.catalog.domain.entity.ItemSeasonId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * itemSeason 테이블에서 item과 season을 다 가져오기 위한 레포지토리
 * author: 김경아
 */
@Repository
public interface ItemSeasonRepository extends JpaRepository<ItemSeason, ItemSeasonId> {

    /**
     * 계절과 분류로 1차로 분류하기 위한 쿼리
     * @param classification 분류(아우터, 상의, 하의)
     * @param seasons 계절(봄, 여름, 가을, 겨울) 리스트
     * @return 분류와 계절에 맞게 필터링된 옷 리스트
     */
    @Query("""
            select i from ItemSeason is
            join is.item i
            join fetch i.category c
            join fetch c.classification cl
            where is.season.seasonName in :seasons AND
            cl.classification = :classification
            """
    )
    List<Item> findByClassificationAndSeason(@Param("classification") String classification, @Param("seasons") List<String> seasons);

    // 특정 상품의 모든 계절 정보 삭제
    void deleteByItem(Item item);

}
