// CategoryService.java
// 카테고리 서비스

package com.fitcaster.weatherfit.catalog.application;

import com.fitcaster.weatherfit.catalog.api.dto.CategoryResponseDTO;
import com.fitcaster.weatherfit.catalog.domain.entity.Category;
import com.fitcaster.weatherfit.catalog.domain.entity.Classification;
import com.fitcaster.weatherfit.catalog.domain.repository.CategoryRepository;
import com.fitcaster.weatherfit.catalog.domain.repository.ClassificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// * author: 김기성
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final ClassificationRepository classificationRepository;
    
    // 분류별 카테고리 목록 조회
    public CategoryResponseDTO getAllCategoriesByClassification() {
        // 모든 분류 조회
        List<Classification> classifications = classificationRepository.findAll();
        
        // 모든 카테고리 조회
        List<Category> categories = categoryRepository.findAll();
        
        // 분류별로 그룹화
        Map<String, List<String>> categoryData = categories.stream()
            .collect(Collectors.groupingBy(
                category -> category.getClassification().getClassification(),
                Collectors.mapping(Category::getCategory, Collectors.toList())
            ));
        
        return CategoryResponseDTO.builder()
            .categoryData(categoryData)
            .build();
    }
}
