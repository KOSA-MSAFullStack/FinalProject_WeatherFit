// ItemService.java
// 상품 서비스
package com.fitcaster.weatherfit.catalog.application;

import com.fitcaster.weatherfit.catalog.api.dto.ItemRequestDTO;
import com.fitcaster.weatherfit.catalog.api.dto.ItemResponseDTO;
import com.fitcaster.weatherfit.catalog.domain.entity.Category;
import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.catalog.domain.repository.CategoryRepository;
import com.fitcaster.weatherfit.catalog.domain.repository.ItemRepository;
import com.fitcaster.weatherfit.common.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

// * author: 김기성
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    // [모든 상품 목록 조회]
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }


    // [상품 이름으로 검색]
    public List<Item> searchItemsByName(String name) {
        return itemRepository.findByNameContainingIgnoreCase(name);
    }


    // [상품 등록]
    @Transactional
    public ItemResponseDTO createItem(ItemRequestDTO.Create request) {
        // 카테고리 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new InternalServerException("Category not found with id: " + request.getCategoryId()));

        // Item 엔티티 생성
        Item item = Item.builder()
                .category(category)
                .itemName(request.getItemName())
                .itemCode(request.getItemCode())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .gender(request.getGender())
                .imageURL(request.getImageURL())
                .aiDescription(request.getAiDescription())
                .maxTemperature(request.getMaxTemperature())
                .minTemperature(request.getMinTemperature())
                .createdAt(LocalDate.now()) // 현재 날짜로 등록일 설정
                .build();

        // Item 저장
        Item savedItem = itemRepository.save(item);

        // ItemResponse로 변환하여 반환
        return ItemResponseDTO.from(savedItem);
    }

    
    // [상품 삭제]
    @Transactional
    public void deleteItem(Long itemId) {
        // 상품 존재 여부 확인
        if (!itemRepository.existsById(itemId)) {
            throw new InternalServerException("Item not found with id: " + itemId);
        }
        itemRepository.deleteById(itemId);
    }
}
