package com.fitcaster.weatherfit.review.application;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.catalog.domain.repository.ItemRepository;
import com.fitcaster.weatherfit.review.api.dto.request.ReviewRequest;
import com.fitcaster.weatherfit.review.api.dto.response.ReviewResponse;
import com.fitcaster.weatherfit.review.api.dto.response.UserReviewResponse;
import com.fitcaster.weatherfit.review.domain.entity.IndoorFit;
import com.fitcaster.weatherfit.review.domain.entity.Review;
import com.fitcaster.weatherfit.review.domain.entity.Temperature;
import com.fitcaster.weatherfit.review.domain.entity.Weather;
import com.fitcaster.weatherfit.review.domain.repository.ReviewRepository;
import com.fitcaster.weatherfit.user.domain.entity.Gender;
import com.fitcaster.weatherfit.user.domain.entity.TemperatureSensitivity;
import com.fitcaster.weatherfit.user.domain.entity.User;
import com.fitcaster.weatherfit.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * author: 이상우
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    /**
     * 현재 로그인한 사용자가 작성한 모든 리뷰 목록을 조회합니다.
     */
    public List<UserReviewResponse> findReviewsByUserId(Long userId) { // 반환 타입 변경
        List<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return reviews.stream()
                .map(UserReviewResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 리뷰를 등록
     */
    public void createReview(Long userId, ReviewRequest request) {
        // 유효성 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. (ID: " + userId + ")"));
        Item item = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. (ID: " + request.itemId() + ")"));

        // 입력 데이터 변환
        Weather weather = Weather.valueOf(request.weather());
        Temperature temperature = Temperature.valueOf(request.weatherSuitability());
        IndoorFit indoorFit = IndoorFit.valueOf(request.breathability());

        // Review 엔티티를 생성
        Review review = Review.builder()
                .user(user)
                .item(item)
                .ratingScore(request.score())
                .contents(request.content())
                .weather(weather)
                .temperature(temperature)
                .indoorFit(indoorFit)
                .build();

        reviewRepository.save(review);
    }

    /**
     * 기존 리뷰를 수정합니다.
     *
     * @param reviewId 수정할 리뷰의 ID
     * @param userId   수정을 요청한 사용자의 ID (권한 확인용)
     * @param request  새로운 리뷰 데이터
     */
    public void updateReview(Long reviewId, Long userId, ReviewRequest request) {
        // 수정할 리뷰를 DB에서 조회합니다.
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 리뷰를 찾을 수 없습니다. (ID: " + reviewId + ")"));

        // 리뷰의 작성자 ID와 수정을 요청한 사용자의 ID가 일치하는지 확인합니다.
        if (!review.getUser().getId().equals(userId)) {
            throw new SecurityException("리뷰를 수정할 권한이 없습니다.");
        }

        // Review 엔티티의 데이터를 업데이트합니다.
        // 엔티티 내에 update 메서드를 만들어 사용하는 것을 권장합니다. (아래 설명 참고)
        review.update(
                request.score(),
                Weather.valueOf(request.weather()),
                Temperature.valueOf(request.weatherSuitability()),
                IndoorFit.valueOf(request.breathability()),
                request.content()
        );
    }

    /**
     * 리뷰를 삭제합니다.
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @param userId   삭제를 요청한 사용자의 ID
     */
    public void deleteReview(Long reviewId, Long userId) {
        // 삭제할 리뷰를 DB에서 조회합니다.
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 리뷰를 찾을 수 없습니다. (ID: " + reviewId + ")"));

        // 리뷰의 작성자 ID와 삭제를 요청한 사용자의 ID가 일치하는지 확인합니다.
        if (!review.getUser().getId().equals(userId)) {
            throw new SecurityException("리뷰를 삭제할 권한이 없습니다.");
        }

        // 리뷰를 삭제합니다.
        reviewRepository.delete(review);
    }

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
