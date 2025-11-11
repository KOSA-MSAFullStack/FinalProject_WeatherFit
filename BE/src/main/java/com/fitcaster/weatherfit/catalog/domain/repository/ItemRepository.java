// ItemRepository.java
// Item 엔티티 데이터 접근 계층

package com.fitcaster.weatherfit.catalog.domain.repository;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// * author: 김기성
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByNameContainingIgnoreCase(String name);
}
