// AdminOrderController.java
// 관리자 주문 관련 API

package com.fitcaster.weatherfit.order.api.controller;

import com.fitcaster.weatherfit.order.api.dto.response.AdminOrderResponseDTO;
import com.fitcaster.weatherfit.order.application.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

// * author: 김기성
@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    // 전체 주문 내역 조회
    @GetMapping
    public ResponseEntity<List<AdminOrderResponseDTO>> getAllOrders() {
        List<AdminOrderResponseDTO> orders = adminOrderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}