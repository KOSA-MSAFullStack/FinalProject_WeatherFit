package com.fitcaster.weatherfit.order.domain.entity;

import com.fitcaster.weatherfit.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID", updatable = false)
    private Long id; // 주문 ID (ORDER_ID)

    // ORDER 테이블의 USER_ID 컬럼
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user; // 유저 (USER_ID)

    @Column(name = "ORDER_DATE", nullable = false)
    private LocalDateTime orderDate; // 주문 일시 (ORDER_DATE)

    @Column(name = "TOTAL_AMOUNT", nullable = false)
    private Integer totalAmount; // 총 결제 금액 (TOTAL_AMOUNT)

    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_STATUS", nullable = false, length = 30)
    private OrderStatus status; // 주문 상태 (ORDER_STATUS)

    @Column(name = "ORDER_NO", nullable = false, unique = true, length = 50)
    private String orderNo; // 주문 번호 (ORDER_NO)

    // 편의상 OrderItem 컬렉션 추가 (주문의 주인이 아님)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 주문 상태 Enum 정의
    public enum OrderStatus {
        ORDER_RECEIVED, // 주문 접수
        ORDER_CONFIRMED, // 주문 확인
        SHIPPING,        // 배송 중
        DELIVERED,       // 배송 완료
        CANCELED         // 주문 취소
    }

    @Builder
    public Order(User user, LocalDateTime orderDate, int totalAmount, OrderStatus status, String orderNo) {
        this.user = user;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderNo = orderNo;
    }

    /**
     * 편의 메서드: OrderItem 리스트를 추가하고 연관관계를 설정합니다.
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this); // OrderItem에도 Order를 설정 (양방향 연관관계)
    }

}