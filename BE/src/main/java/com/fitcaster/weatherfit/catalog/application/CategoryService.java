// CategoryService.java
// 카테고리 서비스

package com.fitcaster.weatherfit.catalog.application;

import com.fitcaster.weatherfit.catalog.api.dto.CategoryRequestDTO;
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
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

// * author: 김기성
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final ClassificationRepository classificationRepository;
    
    // 분류별 카테고리 목록 조회 (categoryId 포함)
    public CategoryResponseDTO getAllCategoriesByClassification() {
        // 모든 카테고리 조회
        List<Category> categories = categoryRepository.findAll();
        
        // 분류별로 그룹화 (categoryId 포함)
        Map<String, List<CategoryResponseDTO.CategoryInfo>> categoryData = categories.stream()
            .collect(Collectors.groupingBy(
                category -> category.getClassification().getClassification(),
                Collectors.mapping(
                    category -> CategoryResponseDTO.CategoryInfo.builder()
                        .categoryId(category.getCategoryId())
                        .category(category.getCategory())
                        .build(),
                    Collectors.toList()
                )
            ));
        
        return CategoryResponseDTO.builder()
            .categoryData(categoryData)
            .build();
    }
    
    // 카테고리 추가
    @Transactional
    public void addCategory(CategoryRequestDTO.Create request) {
        // 분류 조회
        Classification classification = classificationRepository.findByClassification(request.getClassification())
            .orElseThrow(() -> new NoSuchElementException("⚠️ 분류를 찾을 수 없습니다: " + request.getClassification()));
        
        // 중복 확인
        boolean exists = categoryRepository.existsByClassificationAndCategory(classification, request.getCategory());
        if (exists) {
            throw new IllegalArgumentException("⚠️ 이미 존재하는 카테고리입니다: " + request.getCategory());
        }
        
        // 카테고리 저장
        Category category = Category.builder()
            .classification(classification)
            .category(request.getCategory())
            .build();
        
        categoryRepository.save(category);
    }
    
    // 카테고리 수정
    @Transactional
    public void updateCategory(Long categoryId, CategoryRequestDTO.Update request) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new NoSuchElementException("⚠️ 카테고리를 찾을 수 없습니다."));
        
        // 중복 확인 (자기 자신 제외)
        boolean exists = categoryRepository.existsByClassificationAndCategoryAndCategoryIdNot(
            category.getClassification(), 
            request.getNewCategoryName(), 
            categoryId
        );
        
        if (exists) {
            throw new IllegalArgumentException("⚠️ 이미 존재하는 카테고리명입니다: " + request.getNewCategoryName());
        }
        
        category.setCategory(request.getNewCategoryName());
        categoryRepository.save(category);
    }
    
    // 카테고리 삭제
    @Transactional
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NoSuchElementException("⚠️ 카테고리를 찾을 수 없습니다.");
        }
        
        categoryRepository.deleteById(categoryId);
    }
}
