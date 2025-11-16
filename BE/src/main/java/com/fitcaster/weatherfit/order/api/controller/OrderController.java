package com.fitcaster.weatherfit.order.api.controller;

import com.fitcaster.weatherfit.order.application.OrderService;
import com.fitcaster.weatherfit.order.api.dto.request.OrderCreateRequest;
import com.fitcaster.weatherfit.user.domain.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * author: 이상우
 * 주문 관련 API 엔드포인트를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 장바구니에서 선택된 항목을 기반으로 주문을 생성
     * 실제 결제 과정 없이, DB에 주문 정보(ORDERS, ORDER_ITEM)를 저장하고 장바구니를 비움
     * POST /api/orders
     *
     * @param user 인증된 사용자 정보
     * @param request 주문할 장바구니 항목 ID 리스트를 포함하는 요청 DTO
     * @return 201 Created와 생성된 주문 ID
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(@AuthenticationPrincipal User user, @RequestBody @Valid OrderCreateRequest request) {

        // OrderService 호출하여 주문 생성
        Long orderId = orderService.createOrderFromCart(user.getId(), request);

        // 201 Created 상태와 생성된 주문 ID를 응답 본문에 담아 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }
}