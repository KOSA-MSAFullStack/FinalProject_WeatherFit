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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import java.util.List;

// * author: 김기성
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // [모든 상품 목록 조회]
    @GetMapping
    public Page<ItemResponseDTO> getAllItems(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return itemService.getAllItems(pageable);
    }

    // [상품 단건 조회]
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDTO> getItemById(@PathVariable Long itemId) {
        ItemResponseDTO itemResponse = itemService.getItemById(itemId);
        return ResponseEntity.ok(itemResponse);
    }

   // [상품명 검색]
   @GetMapping("/search")
   public List<ItemResponseDTO> searchItemsByName(@RequestParam String itemName) {
       return itemService.searchItemsByName(itemName);
   }
   // [상품명 또는 상품 코드로 검색]
   @GetMapping("/search/keyword")
   public Page<ItemResponseDTO> searchItems(@RequestParam String keyword, Pageable pageable) {
       return itemService.searchItems(keyword, pageable);
   }
}
