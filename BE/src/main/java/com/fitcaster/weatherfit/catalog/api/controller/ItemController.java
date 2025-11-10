// ItemController.java
// 상품 관련 API 엔드포인트

package com.fitcaster.weatherfit.catalog.api.controller;

import com.fitcaster.weatherfit.catalog.application.ItemListService;
import com.fitcaster.weatherfit.catalog.api.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemListService itemListService;

    // 모든 상품 목록 조회
    @GetMapping
    public List<ItemResponse> getAllItems() {
        return itemListService.getAllItems().stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }
}
