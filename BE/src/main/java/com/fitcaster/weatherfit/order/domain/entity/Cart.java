package com.fitcaster.weatherfit.order.domain.entity;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;


/**
 * author: 이상우
 */
@Entity
@Table(name = "CART")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_ID", nullable = false)
    private Long id;

    // 'USERS' 테이블과의 Many-to-One 관계 (USER_ID)
    // 실제 USERS 엔티티 클래스가 필요합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    // 'ITEM' 테이블과의 Many-to-One 관계 (ITEM_ID)
    // 실제 ITEM 엔티티 클래스가 필요합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private Item item;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity; // 담긴 수량

    /**
     * 장바구니 상품 수량 변경
     * @param quantity 새로 변경할 수량
     */
    public void updateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("상품 수량은 1개 이상이어야 합니다.");
        }
        this.quantity = quantity;
    }

}
