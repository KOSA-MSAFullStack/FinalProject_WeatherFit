// AdminOrderService.java
// 관리자 주문 서비스

package com.fitcaster.weatherfit.order.application;

import com.fitcaster.weatherfit.order.api.dto.response.AdminOrderResponseDTO;
import com.fitcaster.weatherfit.order.domain.entity.Order;
import com.fitcaster.weatherfit.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// * author: 김기성
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOrderService {

    private final OrderRepository orderRepository;

    // 전체 주문 내역 조회 (페이징)
    public AdminOrderResponseDTO.PagedResponse getAllOrders(int page, int size, String sortBy) {
        try {
            // 정렬 기준 설정 (기본: 주문일시 내림차순)
            Sort sort = Sort.by(Sort.Direction.DESC, "orderDate");
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Order> orderPage = orderRepository.findAllOrdersWithDetails(pageable);
            List<AdminOrderResponseDTO> orders = AdminOrderResponseDTO.fromOrdersPage(orderPage);
            
            return AdminOrderResponseDTO.PagedResponse.builder()
                    .orders(orders)
                    .currentPage(orderPage.getNumber())
                    .totalPages(orderPage.getTotalPages())
                    .totalElements(orderPage.getTotalElements())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("주문 내역 조회 실패", e);
        }
    }

    // 주문번호/고객명 검색 (페이징)
    public AdminOrderResponseDTO.PagedResponse searchOrders(String keyword, int page, int size) {
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "orderDate");
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Order> orderPage = orderRepository.searchOrders(keyword, pageable);
            List<AdminOrderResponseDTO> orders = AdminOrderResponseDTO.fromOrdersPage(orderPage);
            
            return AdminOrderResponseDTO.PagedResponse.builder()
                    .orders(orders)
                    .currentPage(orderPage.getNumber())
                    .totalPages(orderPage.getTotalPages())
                    .totalElements(orderPage.getTotalElements())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("주문 검색 실패", e);
        }
    }
}