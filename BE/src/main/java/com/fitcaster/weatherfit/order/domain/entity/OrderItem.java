package com.fitcaster.weatherfit.order.domain.entity;

import com.fitcaster.weatherfit.catalog.domain.entity.Item; // Item 엔티티가 있다고 가정
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ORDER_ITEM")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ITEM_ID", updatable = false)
    private Long id; // 주문 상세 상품 ID (ORDER_ITEM_ID)

    // ORDER 테이블과의 관계 (FK: ORDER_ID)
    @Setter(AccessLevel.PACKAGE) // Order.addOrderItem()에서만 사용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false)
    private Order order; // 주문 (ORDER_ID)

    // ITEM 테이블과의 관계 (FK: ITEM_ID)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private Item item; // 상품 (ITEM_ID)

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity; // 주문 수량 (QUANTITY)


    @Builder
    public OrderItem(Order order, Item item, Integer quantity) {
        this.order = order;
        this.item = item;
        this.quantity = quantity;
    }

    /**
     * 정적 팩토리 메서드: OrderItem 생성 로직을 캡슐화
     */
    public static OrderItem createOrderItem(Order order, Item item, Integer quantity) {
        return OrderItem.builder()
                .order(order)
                .item(item)
                .quantity(quantity)
                .build();
    }
}
