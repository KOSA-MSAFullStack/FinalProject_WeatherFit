// CategoryController.java
// 카테고리 API 컨트롤러

package com.fitcaster.weatherfit.catalog.api.controller;

import com.fitcaster.weatherfit.catalog.api.dto.CategoryResponseDTO;
import com.fitcaster.weatherfit.catalog.application.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// * author: 김기성
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    
    // [분류별 카테고리 목록 조회]
    @GetMapping
    public ResponseEntity<CategoryResponseDTO> getAllCategories() {
        CategoryResponseDTO response = categoryService.getAllCategoriesByClassification();
        return ResponseEntity.ok(response);
    }
}
