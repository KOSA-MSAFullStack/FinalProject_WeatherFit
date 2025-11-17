// AdminOrderService.java
// 관리자 주문 서비스

package com.fitcaster.weatherfit.order.application;

import com.fitcaster.weatherfit.order.api.dto.response.AdminOrderResponseDTO;
import com.fitcaster.weatherfit.order.domain.entity.Order;
import com.fitcaster.weatherfit.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// * author: 김기성
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderService {

    private final OrderRepository orderRepository;

    // 전체 주문 내역 조회
    public List<AdminOrderResponseDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAllOrdersWithDetails();
        return AdminOrderResponseDTO.fromOrders(orders);
    }
}