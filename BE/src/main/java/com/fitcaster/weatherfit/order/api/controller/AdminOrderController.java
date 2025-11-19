// AdminOrderController.java
// 관리자 주문 관련 API

package com.fitcaster.weatherfit.order.api.controller;

import com.fitcaster.weatherfit.order.api.dto.response.AdminOrderResponseDTO;
import com.fitcaster.weatherfit.order.application.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// * author: 김기성
@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    // 전체 주문 내역 조회 (페이징)
    @GetMapping
    public ResponseEntity<AdminOrderResponseDTO.PagedResponse> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "orderDate") String sort) {
        AdminOrderResponseDTO.PagedResponse response = adminOrderService.getAllOrders(page, size, sort);
        return ResponseEntity.ok(response);
    }

    // 주문번호/고객명 검색 (페이징)
    @GetMapping("/search")
    public ResponseEntity<AdminOrderResponseDTO.PagedResponse> searchOrders(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        AdminOrderResponseDTO.PagedResponse response = adminOrderService.searchOrders(keyword, page, size);
        return ResponseEntity.ok(response);
    }
}