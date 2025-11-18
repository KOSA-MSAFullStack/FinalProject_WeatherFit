package com.fitcaster.weatherfit.order.application;

import com.fitcaster.weatherfit.order.api.dto.response.OrderHistoryResponse;
import com.fitcaster.weatherfit.order.domain.entity.Order;
import com.fitcaster.weatherfit.order.domain.entity.OrderItem;
import com.fitcaster.weatherfit.order.domain.entity.Cart;
import com.fitcaster.weatherfit.order.domain.repository.OrderRepository;
import com.fitcaster.weatherfit.order.domain.repository.OrderItemRepository;
import com.fitcaster.weatherfit.order.domain.repository.CartRepository;
import com.fitcaster.weatherfit.order.api.dto.request.OrderCreateRequest;
import com.fitcaster.weatherfit.user.domain.entity.User;
import com.fitcaster.weatherfit.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * author: 이상우
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    /**
     * 장바구니 항목을 기반으로 주문을 생성하고 장바구니를 비웁니다.
     * @param userId 주문하는 사용자 ID
     * @param request 주문할 장바구니 항목 ID 리스트
     * @return 생성된 주문 ID
     */
    @Transactional
    public Long createOrderFromCart(Long userId, OrderCreateRequest request) {
        // 주문할 장바구니 항목들을 조회
        List<Cart> cartItems = cartRepository.findAllByIdInAndUserId(request.getCartItemIds(), userId);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("주문할 유효한 장바구니 항목이 없습니다.");
        }

        // 총 주문 금액 계산
        int totalAmount = cartItems.stream()
                .mapToInt(item -> item.getItem().getPrice() * item.getQuantity())
                .sum();

        // ORDER 엔티티 생성 및 저장
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. (ID: " + userId + ")"));

        Order newOrder = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .totalAmount(totalAmount)
                .status(Order.OrderStatus.ORDER_RECEIVED) // 주문 접수 상태
                .orderNo(generateOrderNo())
                .build();
        orderRepository.save(newOrder);

        // ORDER_ITEM 엔티티 생성 및 저장
        List<OrderItem> orderItems = cartItems.stream()
                .map(cart -> {
                    // Item 엔티티도 Cart 엔티티에 접근 가능하다고 가정
                    return OrderItem.createOrderItem(
                            newOrder,
                            cart.getItem(),
                            cart.getQuantity()
                    );
                })
                .collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);

        // 주문이 성공적으로 생성되었으므로, 해당 장바구니 항목들을 삭제
        cartRepository.deleteAll(cartItems);

        return newOrder.getId();
    }

    /**
     * 주문 내역을 조회하기 위한 메서드
     * @param userId 주문하는 사용자 ID
     * @return 주문 내역 조회 DTO
     */
    @Transactional(readOnly = true)
    public Page<OrderHistoryResponse> getOrderHistory(Long userId, Pageable pageable) {
        // Repository로부터 Page<OrderItem> 객체를 받음
        Page<Order> orderPage = orderRepository.findOrdersByUserId(userId, pageable);

        // 조회된 Order들의 ID만 리스트로 추출합니다.
        List<Long> orderIds = orderPage.getContent().stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        // 만약 조회된 주문이 없다면, 빈 페이지를 그대로 반환합니다.
        if (orderIds.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Order> ordersWithDetails = orderRepository.findOrdersWithDetailsByIds(orderIds);

        List<OrderHistoryResponse> dtos = ordersWithDetails.stream()
                .map(OrderHistoryResponse::from)
                .collect(Collectors.toList());

        // Page.map()을 사용하여 내용물(content)만 DTO로 변환
        // 이 과정에서 페이징 정보(전체 개수, 페이지 번호 등)는 그대로 유지됨
        return new PageImpl<>(dtos, pageable, orderPage.getTotalElements());
    }

    /**
     * UUID 기반의 주문 번호를 생성
     * (예: 550e8400-e29b-41d4-a716-446655440000)
     * @return 생성된 UUID 문자열
     */
    private String generateOrderNo() {
        // UUID를 생성하고 문자열로 반환
        return UUID.randomUUID().toString();
    }
}