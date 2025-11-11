// ItemService.java
// [상품 서비스]

package com.fitcaster.weatherfit.catalog.application;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.catalog.domain.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    // 모든 상품 목록 조회
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
}
