// AdminItemController.java
// 관리자 상품 관련 API

package com.fitcaster.weatherfit.catalog.api.controller;

import com.fitcaster.weatherfit.catalog.ai.api.dto.AIRequestDTO;
import com.fitcaster.weatherfit.catalog.ai.api.dto.AIResponseDTO;
import com.fitcaster.weatherfit.catalog.ai.application.AIService;
import com.fitcaster.weatherfit.catalog.api.dto.ItemRequestDTO;
import com.fitcaster.weatherfit.catalog.api.dto.ItemResponseDTO;
import com.fitcaster.weatherfit.catalog.application.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.http.MediaType;

// * author: 김기성
@RestController
@RequestMapping("/api/admin/items")
@RequiredArgsConstructor
public class AdminItemController {

    private final ItemService itemService;
    private final AIService aiService;

    // [상품 등록]
    @PostMapping
    public ResponseEntity<ItemResponseDTO> createItem(@RequestBody ItemRequestDTO.Create request) {
        // ItemService를 통해 상품 등록
        ItemResponseDTO itemResponse = itemService.createItem(request);
        // 등록된 상품 정보와 함께 201 Created 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(itemResponse);
    }

    // [AI 설명 생성]
    @PostMapping(value = "/generate-description", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AIResponseDTO> generateAIDescription(@ModelAttribute AIRequestDTO request) {
        String aiDescription = aiService.generateDescription(request);
        return ResponseEntity.ok(new AIResponseDTO(aiDescription));
    }

    // [상품 수정]
    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDTO> updateItem(@PathVariable Long itemId, @RequestBody ItemRequestDTO.Update request) {
        ItemResponseDTO updatedItem = itemService.updateItem(itemId, request);
        return ResponseEntity.ok(updatedItem);
    }

    // [상품 삭제]
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}