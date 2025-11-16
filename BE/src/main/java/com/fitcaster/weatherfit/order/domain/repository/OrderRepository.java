package com.fitcaster.weatherfit.order.domain.repository;

import com.fitcaster.weatherfit.order.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
