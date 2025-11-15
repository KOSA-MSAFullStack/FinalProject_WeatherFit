package com.fitcaster.weatherfit.review.application;

import com.fitcaster.weatherfit.review.api.dto.response.ReviewResponse;
import com.fitcaster.weatherfit.review.domain.entity.Review;
import com.fitcaster.weatherfit.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * author: 이상우
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    /**
     * 특정 상품에 대한 리뷰 목록을 페이징하여 조회합니다.
     *
     * @param itemId   조회할 상품의 ID
     * @param pageable 페이징 및 정렬 정보 (e.g., page=0, size=10, sort=createdAt,desc)
     * @return 페이징된 리뷰 DTO 목록
     */
    public Page<ReviewResponse> findReviewsByItemId(Long itemId, Pageable pageable) {
        // Repository를 통해 페이징된 Review 엔티티 목록을 조회
        Page<Review> reviewPage = reviewRepository.findByItemId(itemId, pageable);

        // 조회된 엔티티 목록(Page<Review>)을 DTO 목록(Page<ReviewResponse>)으로 변환
        // Page 객체의 map 메서드를 사용하면 페이징 정보는 유지하면서 내용물(엔티티)만 DTO로 손쉽게 변경할 수 있습니다.
        return reviewPage.map(ReviewResponse::from);
    }
}
