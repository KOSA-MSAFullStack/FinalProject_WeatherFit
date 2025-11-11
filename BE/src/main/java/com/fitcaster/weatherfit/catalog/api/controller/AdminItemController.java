// AdminItemController.java
// 관리자 상품 관련 API

package com.fitcaster.weatherfit.catalog.api.controller;

import com.fitcaster.weatherfit.catalog.api.dto.ItemRequestDTO;
import com.fitcaster.weatherfit.catalog.api.dto.ItemResponseDTO;
import com.fitcaster.weatherfit.catalog.application.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// * author: 김기성
@RestController
@RequestMapping("/api/admin/items")
@RequiredArgsConstructor
public class AdminItemController {

    private final ItemService itemService;

    // [상품 등록]
    @PostMapping
    public ResponseEntity<ItemResponseDTO> createItem(@RequestBody ItemRequestDTO.Create request) {
        // ItemService를 통해 상품 등록
        ItemResponseDTO itemResponse = itemService.createItem(request);
        // 등록된 상품 정보와 함께 201 Created 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(itemResponse);
    }

    // [상품 삭제]
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}