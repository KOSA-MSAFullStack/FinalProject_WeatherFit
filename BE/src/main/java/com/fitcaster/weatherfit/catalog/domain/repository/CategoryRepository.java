// CategoryRepository.java
// Category 엔티티 데이터 접근 계층

package com.fitcaster.weatherfit.catalog.domain.repository;

import com.fitcaster.weatherfit.catalog.domain.entity.Category;
import com.fitcaster.weatherfit.catalog.domain.entity.Classification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// * author: 김기성
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategory(String category);
    
    // 분류와 카테고리명으로 존재 여부 확인
    boolean existsByClassificationAndCategory(Classification classification, String category);
    
    // 분류와 카테고리명으로 존재 여부 확인 (특정 ID 제외)
    boolean existsByClassificationAndCategoryAndCategoryIdNot(Classification classification, String category, Long categoryId);
}
