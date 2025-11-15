// ItemRepository.java
// Item 엔티티 데이터 접근 계층

package com.fitcaster.weatherfit.catalog.domain.repository;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// * author: 김기성
public interface ItemRepository extends JpaRepository<Item, Long> {
    // [전체 상품 최신순 조회]
    List<Item> findAllByOrderByCreatedAtDesc();

    // [상품명 검색]
   List<Item> findByItemNameContainingIgnoreCase(String itemName);
}
