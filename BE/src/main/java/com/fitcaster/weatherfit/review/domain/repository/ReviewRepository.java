package com.fitcaster.weatherfit.review.domain.repository;

import com.fitcaster.weatherfit.review.api.dto.response.EnumStatisticsDto;
import com.fitcaster.weatherfit.review.api.dto.response.ReviewStatisticsDto;
import com.fitcaster.weatherfit.review.domain.entity.IndoorFit;
import com.fitcaster.weatherfit.review.domain.entity.Review;
import com.fitcaster.weatherfit.review.domain.entity.Temperature;
import com.fitcaster.weatherfit.review.domain.entity.Weather;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 특정 상품(itemId)에 대한 리뷰 목록을 페이징하여 조회합니다.
     * fetch join을 사용하여 N+1 문제를 방지합니다.
     * @param itemId 상품 ID
     * @param pageable 페이징 정보 (페이지 번호, 페이지 크기, 정렬 기준)
     * @return 페이징된 리뷰 목록
     */
    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.item.itemId = :itemId")
    Page<Review> findByItemId(@Param("itemId") Long itemId, Pageable pageable);

    // 상품 ID에 대한 전체 리뷰 수와 평균 별점을 한 번에 조회
    @Query("SELECT new com.fitcaster.weatherfit.review.api.dto.response.ReviewStatisticsDto(COUNT(r), AVG(r.ratingScore)) " +
            "FROM Review r WHERE r.item.itemId = :itemId")
    ReviewStatisticsDto findReviewStatisticsByItemId(@Param("itemId") Long itemId);

    // 날씨(Weather)별 리뷰 수 통계 조회
    @Query("SELECT new com.fitcaster.weatherfit.review.api.dto.response.EnumStatisticsDto(r.weather, COUNT(r)) " +
            "FROM Review r WHERE r.item.itemId = :itemId GROUP BY r.weather")
    List<EnumStatisticsDto<Weather>> findWeatherStatisticsByItemId(@Param("itemId") Long itemId);

    // 체감 온도(Temperature)별 리뷰 수 통계 조회
    @Query("SELECT new com.fitcaster.weatherfit.review.api.dto.response.EnumStatisticsDto(r.temperature, COUNT(r)) " +
            "FROM Review r WHERE r.item.itemId = :itemId GROUP BY r.temperature")
    List<EnumStatisticsDto<Temperature>> findTemperatureStatisticsByItemId(@Param("itemId") Long itemId);

    // 실내 활동 적합도(IndoorFit)별 리뷰 수 통계 조회
    @Query("SELECT new com.fitcaster.weatherfit.review.api.dto.response.EnumStatisticsDto(r.indoorFit, COUNT(r)) " +
            "FROM Review r WHERE r.item.itemId = :itemId GROUP BY r.indoorFit")
    List<EnumStatisticsDto<IndoorFit>> findIndoorFitStatisticsByItemId(@Param("itemId") Long itemId);

    /**
     * 특정 사용자 ID(userId)를 기준으로 해당 사용자가 작성한 모든 리뷰를 조회합니다.
     * 결과는 생성일(createdAt)을 기준으로 내림차순(최신순)으로 정렬됩니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 해당 사용자가 작성한 리뷰 목록
     */
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 상품의 모든 리뷰 최신순 조회 (// * author: 김기성)
    // AI 요약을 위해 모든 리뷰 로드
    @Query("SELECT r FROM Review r WHERE r.item.itemId = :itemId ORDER BY r.createdAt DESC")
    List<Review> findAllByItemIdOrderByCreatedAtDesc(@Param("itemId") Long itemId);
}
