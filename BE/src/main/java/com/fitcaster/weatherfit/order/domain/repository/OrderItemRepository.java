package com.fitcaster.weatherfit.order.domain.repository;

import com.fitcaster.weatherfit.order.domain.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * 특정 사용자의 주문 내역을 주문 상품(OrderItem) 단위로 조회
     * FETCH JOIN을 사용하여 Order 엔티티와 Item 엔티티를 함께 로딩하여
     * N+1 문제를 방지하고 성능을 최적화합니다.
     *
     * @param userId 조회할 회원의 ID
     * @param pageable 페이징 정보 (ex: 페이지 번호, 크기, 정렬)
     * @return 페이징 처리된 주문 항목 리스트 (OrderItem)
     */
    @Query("SELECT oi FROM OrderItem oi " +
            "JOIN FETCH oi.order o " +
            "JOIN FETCH oi.item i " +
            "WHERE o.user.id = :userId ")
    Page<OrderItem> findOrderItemsWithOrderAndItemByUserId(@Param("userId") Long userId, Pageable pageable);
}
