package com.fitcaster.weatherfit.order.domain.repository;

import com.fitcaster.weatherfit.order.domain.entity.Order;
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