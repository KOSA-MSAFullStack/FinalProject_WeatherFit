package com.fitcaster.weatherfit.catalog.domain.repository;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
//    List<Item> findByClassificationAndSeason(@Param("category") Classification classification, @Param("season") Season season);
}
