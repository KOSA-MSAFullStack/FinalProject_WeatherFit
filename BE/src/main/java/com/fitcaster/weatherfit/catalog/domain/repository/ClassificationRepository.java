// ClassificationRepository.java
// Classification 엔티티 데이터 접근 계층

package com.fitcaster.weatherfit.catalog.domain.repository;

import com.fitcaster.weatherfit.catalog.domain.entity.Classification;
import org.springframework.data.jpa.repository.JpaRepository;

// * author: 김기성
public interface ClassificationRepository extends JpaRepository<Classification, Long> {
}
