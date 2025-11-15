// SeasonRepository.java
// Season 엔티티 데이터 접근 계층

package com.fitcaster.weatherfit.catalog.domain.repository;

import com.fitcaster.weatherfit.catalog.domain.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// * author: 김기성
public interface SeasonRepository extends JpaRepository<Season, Long> {
    Optional<Season> findBySeasonName(String seasonName);
}
