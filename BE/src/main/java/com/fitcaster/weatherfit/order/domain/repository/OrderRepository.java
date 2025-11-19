package com.fitcaster.weatherfit.order.domain.repository;

import com.fitcaster.weatherfit.order.domain.entity.Order;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH oi.item i " +
            "WHERE o.user.id = :userId",
            countQuery = "SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    Page<Order> findOrdersByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN FETCH o.user u " + // 주문한 사용자
            "JOIN FETCH o.orderItems oi " + // 주문 아이템
            "JOIN FETCH oi.item i " + // 아이템 상세
            "LEFT JOIN FETCH i.reviews r " + // 아이템의 리뷰들 (없을 수 있으므로 LEFT JOIN)
            "LEFT JOIN FETCH r.user ru " + // 리뷰를 쓴 사용자 (없을 수 있으므로 LEFT JOIN)
            "WHERE o.id IN :orderIds") // ID 목록으로 조회
    List<Order> findOrdersWithDetailsByIds(@Param("orderIds") List<Long> orderIds);

    // * author: 김기성
    // 전체 주문 조회 (최신순)
    @Query(value = "SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.item " +
            "LEFT JOIN FETCH o.user",
            countQuery = "SELECT COUNT(DISTINCT o) FROM Order o")
    Page<Order> findAllOrdersWithDetails(Pageable pageable);

    // 주문번호 또는 고객명으로 검색
    @Query(value = "SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.item " +
            "LEFT JOIN FETCH o.user u " +
            "WHERE o.orderNo LIKE %:keyword% OR u.name LIKE %:keyword%",
            countQuery = "SELECT COUNT(DISTINCT o) FROM Order o " +
            "LEFT JOIN o.user u " +
            "WHERE o.orderNo LIKE %:keyword% OR u.name LIKE %:keyword%")
    Page<Order> searchOrders(@Param("keyword") String keyword, Pageable pageable);
}