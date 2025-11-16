package com.fitcaster.weatherfit.order.domain.repository;

import com.fitcaster.weatherfit.order.domain.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
