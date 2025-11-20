package com.fitcaster.weatherfit.review.api.controller;

import com.fitcaster.weatherfit.review.api.dto.request.ReviewRequest;
import com.fitcaster.weatherfit.review.api.dto.response.ReviewResponse;
import com.fitcaster.weatherfit.review.api.dto.response.ReviewSummaryResponse;
import com.fitcaster.weatherfit.review.api.dto.response.UserReviewResponse;
import com.fitcaster.weatherfit.review.application.ReviewService;
import com.fitcaster.weatherfit.user.domain.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * author: 이상우
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 내가 쓴 리뷰 목록 조회 API
     */
    @GetMapping()
    public ResponseEntity<List<UserReviewResponse>> getMyReviews(@AuthenticationPrincipal User user) {
        List<UserReviewResponse> myReviews = reviewService.findReviewsByUserId(user.getId());
        return ResponseEntity.ok(myReviews);
    }

    /**
     * 리뷰 등록 API
     * HTTP Method: POST
     * URL: /api/reviews
     * @param request 리뷰 생성에 필요한 데이터 (JSON)
     * @param user 현재 로그인한 사용자 정보 (Spring Security가 주입)
     * @return 생성 성공 시 HTTP 201 Created 응답
     */
    @PostMapping
    public ResponseEntity<Void> createReview(@Valid @RequestBody ReviewRequest request, @AuthenticationPrincipal User user) {
        reviewService.createReview(user.getId(), request);
        // 201 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 리뷰 수정 API
     * HTTP Method: PUT
     * URL: /api/reviews/{reviewId}
     * @param reviewId 수정할 리뷰의 ID (URL 경로에서 추출)
     * @param request  리뷰 수정에 필요한 데이터 (JSON)
     * @param user     현재 로그인한 사용자 정보 (권한 확인용)
     * @return 성공 시 HTTP 200 OK 응답
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(@PathVariable Long reviewId,
                                             @Valid @RequestBody ReviewRequest request,
                                             @AuthenticationPrincipal User user) {
        reviewService.updateReview(reviewId, user.getId(), request);

        // 성공적으로 처리되었음을 알리는 HTTP 200 응답 반환
        return ResponseEntity.ok().build();
    }

    /**
     * 리뷰 삭제 API
     * HTTP Method: DELETE
     * URL: /api/reviews/{reviewId}
     * @param reviewId 삭제할 리뷰의 ID (URL 경로에서 추출)
     * @param user     현재 로그인한 사용자 정보 (권한 확인용)
     * @return 성공 시 HTTP 204 No Content 응답
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId, @AuthenticationPrincipal User user) {
        reviewService.deleteReview(reviewId, user.getId());

        // 내용 없이 성공적으로 삭제되었음을 알리는 HTTP 204 응답 반환
        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 리뷰 조회 API
     */
    @GetMapping("/items/{itemId}")
    public ResponseEntity<ReviewSummaryResponse> getReviewsByItem(@PathVariable Long itemId, @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable) {

        ReviewSummaryResponse response = reviewService.findReviewsAndStatisticsByItemId(itemId, pageable);
        return ResponseEntity.ok(response);
    }
}
