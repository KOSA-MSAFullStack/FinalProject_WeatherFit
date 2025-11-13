// ItemController.java
// 상품 관련 API 엔드포인트

package com.fitcaster.weatherfit.catalog.api.controller;

import com.fitcaster.weatherfit.catalog.application.ItemService;
import com.fitcaster.weatherfit.catalog.api.dto.ItemResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

// * author: 김기성
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // [모든 상품 목록 조회]
    @GetMapping
    public List<ItemResponseDTO> getAllItems() {
        return itemService.getAllItems().stream()
                .map(ItemResponseDTO::from)
                .collect(Collectors.toList());
    }

    // [상품 단건 조회]
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDTO> getItemById(@PathVariable Long itemId) {
        ItemResponseDTO itemResponse = itemService.getItemById(itemId);
        return ResponseEntity.ok(itemResponse);
    }

    // [상품명 검색]
    @GetMapping("/search")
    public List<ItemResponseDTO> searchItems(@RequestParam String name) {
        return itemService.searchItemsByName(name).stream()
                .map(ItemResponseDTO::from)
                .collect(Collectors.toList());
    }
}
