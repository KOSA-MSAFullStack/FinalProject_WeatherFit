// ReviewSummaryService.java
// [AI 리뷰 요약 비동기 처리 서비스]

package com.fitcaster.weatherfit.review.application;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.catalog.domain.repository.ItemRepository;
import com.fitcaster.weatherfit.review.ai.application.ReviewAIService;
import com.fitcaster.weatherfit.review.domain.entity.Review;
import com.fitcaster.weatherfit.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// * author: 김기성
@Service
@RequiredArgsConstructor
public class ReviewSummaryService {

    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewAIService reviewAIService;

    // 특정 상품의 모든 리뷰 조회 후 AI 요약 생성 및 상품 정보 업데이트
    // 비동기 실행, 요청 처리 영향 최소화
    @Async
    @Transactional
    public void updateAiReviewSummary(Long itemId) {
        // 상품 ID로 상품 조회, 없을 경우 예외 발생
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. (ID: " + itemId + ")"));
        
        // 해당 상품의 모든 리뷰 최신순 조회
        List<Review> allReviews = reviewRepository.findAllByItemIdOrderByCreatedAtDesc(item.getItemId());

        // 리뷰 목록 AI 서비스에 전달하여 요약 생성
        String summary = reviewAIService.summarizeReviews(allReviews);

        // 생성된 AI 요약 상품 엔티티에 설정
        item.setReviewAiSummary(summary);

        // 변경된 상품 정보 저장
        itemRepository.save(item);
    }
}
