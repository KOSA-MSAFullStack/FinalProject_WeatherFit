// ItemRepository.java
// Item 엔티티 데이터 접근 계층

package com.fitcaster.weatherfit.catalog.domain.repository;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// * author: 김기성
public interface ItemRepository extends JpaRepository<Item, Long> {
    // 상품명 검색
    List<Item> findByItemNameContainingIgnoreCase(String itemName);
    // 상품명 또는 상품 코드로 검색 (페이지네이션)
    Page<Item> findByItemNameContainingIgnoreCaseOrItemCodeContainingIgnoreCase(String itemName, String itemCode, Pageable pageable);
}
