package com.fitcaster.weatherfit.review.application;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.catalog.domain.repository.ItemRepository;
import com.fitcaster.weatherfit.review.api.dto.request.ReviewRequest;
import com.fitcaster.weatherfit.review.api.dto.response.*;
import com.fitcaster.weatherfit.review.domain.entity.IndoorFit;
import com.fitcaster.weatherfit.review.domain.entity.Review;
import com.fitcaster.weatherfit.review.domain.entity.Temperature;
import com.fitcaster.weatherfit.review.domain.entity.Weather;
import com.fitcaster.weatherfit.review.domain.repository.ReviewRepository;
import com.fitcaster.weatherfit.user.domain.entity.User;
import com.fitcaster.weatherfit.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
    private final ReviewSummaryService reviewSummaryService;


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

        // AI 리뷰 요약 생성 및 업데이트 - 비동기 호출 (// * author: 김기성)
        reviewSummaryService.updateAiReviewSummary(item.getItemId());
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
        review.update(
                request.score(),
                Weather.valueOf(request.weather()),
                Temperature.valueOf(request.weatherSuitability()),
                IndoorFit.valueOf(request.breathability()),
                request.content()
        );

        // AI 리뷰 요약 업데이트 - 비동기 호출 (// * author: 김기성)
        reviewSummaryService.updateAiReviewSummary(review.getItem().getItemId());
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

        Item item = review.getItem();

        // 리뷰를 삭제합니다.
        reviewRepository.delete(review);

        // AI 리뷰 요약 업데이트 - 비동기 호출 (// * author: 김기성)
        reviewSummaryService.updateAiReviewSummary(item.getItemId());
    }

    /**
     * 특정 상품에 대한 리뷰 목록을 페이징하여 조회합니다.
     *
     * @param itemId   조회할 상품의 ID
     * @param pageable 페이징 및 정렬 정보 (e.g., page=0, size=10, sort=createdAt,desc)
     * @return 페이징된 리뷰 DTO 목록
     */
    @Transactional(readOnly = true)
    public ReviewSummaryResponse findReviewsAndStatisticsByItemId(Long itemId, Pageable pageable) {
        // 기존 리뷰 페이징 조회
        Page<Review> reviewPage = reviewRepository.findByItemId(itemId, pageable);
        Page<ReviewResponse> reviewResponsePage = reviewPage.map(ReviewResponse::from);

        // 평균 별점 및 리뷰 건수 조회
        ReviewStatisticsDto statistics = reviewRepository.findReviewStatisticsByItemId(itemId);

        // 각 Enum 타입별 통계 조회
        Map<Weather, Long> weatherStats = reviewRepository.findWeatherStatisticsByItemId(itemId).stream()
                .collect(Collectors.toMap(EnumStatisticsDto::getEnumValue, EnumStatisticsDto::getCount));
        Map<Temperature, Long> temperatureStats = reviewRepository.findTemperatureStatisticsByItemId(itemId).stream()
                .collect(Collectors.toMap(EnumStatisticsDto::getEnumValue, EnumStatisticsDto::getCount));
        Map<IndoorFit, Long> indoorFitStats = reviewRepository.findIndoorFitStatisticsByItemId(itemId).stream()
                .collect(Collectors.toMap(EnumStatisticsDto::getEnumValue, EnumStatisticsDto::getCount));

        // Item 엔티티에서 AI 요약 가져오기 (// * author: 김기성)
        String aiSummary = itemRepository.findById(itemId)
                .map(Item::getReviewAiSummary)
                .orElse("리뷰 요약을 찾을 수 없습니다.");

        // 조회한 모든 정보를 새로운 DTO에 담아 반환
        return new ReviewSummaryResponse(
                statistics.getTotalCount(),
                statistics.getAverageRating(),
                weatherStats,
                temperatureStats,
                indoorFitStats,
                reviewResponsePage,
                aiSummary
        );
    }
}
