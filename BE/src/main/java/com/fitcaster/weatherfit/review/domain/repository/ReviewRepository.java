package com.fitcaster.weatherfit.review.domain.repository;

import com.fitcaster.weatherfit.review.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 특정 상품(itemId)에 대한 리뷰 목록을 페이징하여 조회합니다.
     * fetch join을 사용하여 N+1 문제를 방지합니다.
     * @param itemId 상품 ID
     * @param pageable 페이징 정보 (페이지 번호, 페이지 크기, 정렬 기준)
     * @return 페이징된 리뷰 목록
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.item.id = :itemId")
    Page<Review> findByItemId(@Param("itemId") Long itemId, Pageable pageable);
}
