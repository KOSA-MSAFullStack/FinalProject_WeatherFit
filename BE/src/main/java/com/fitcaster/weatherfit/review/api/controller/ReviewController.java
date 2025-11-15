package com.fitcaster.weatherfit.review.api.controller;

import com.fitcaster.weatherfit.review.api.dto.response.ReviewResponse;
import com.fitcaster.weatherfit.review.application.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: 이상우
 */
@RestController
@RequestMapping("/api/v1/items/{itemId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getReviewsByItem(
            @PathVariable Long itemId,
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable) {

        Page<ReviewResponse> reviews = reviewService.findReviewsByItemId(itemId, pageable);
        return ResponseEntity.ok(reviews);
    }
}
