package com.fitcaster.weatherfit.review.domain.repository;

import com.fitcaster.weatherfit.review.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
