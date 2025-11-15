// CategoryRepository.java
// Category 엔티티 데이터 접근 계층

package com.fitcaster.weatherfit.catalog.domain.repository;

import com.fitcaster.weatherfit.catalog.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// * author: 김기성
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategory(String category);
}
