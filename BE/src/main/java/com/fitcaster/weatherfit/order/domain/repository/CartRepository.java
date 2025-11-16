package com.fitcaster.weatherfit.order.domain.repository;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.order.domain.entity.Cart;
import com.fitcaster.weatherfit.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * 특정 사용자(User)의 장바구니에서 특정 상품(Item)이 존재하는지 확인합니다.
     * 장바구니에 상품을 추가할 때 중복 검사에 사용됩니다.
     */
    Optional<Cart> findByUserAndItem(User user, Item item);

    /**
     * 특정 사용자(User)의 장바구니에 담긴 모든 항목을 조회합니다.
     * 장바구니 목록 페이지를 보여줄 때 사용됩니다.
     */
    List<Cart> findByUser(User user);

    // 주문 생성 시 필요한 장바구니 항목 조회
    @Query("SELECT c FROM Cart c JOIN FETCH c.item i WHERE c.id IN :cartItemIds AND c.user.id = :userId")
    List<Cart> findAllByIdInAndUserId(@Param("cartItemIds") List<Long> cartItemIds, @Param("userId") Long userId);

}
