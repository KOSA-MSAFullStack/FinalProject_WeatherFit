package com.fitcaster.weatherfit.order.application;

import com.fitcaster.weatherfit.catalog.domain.entity.Item;
import com.fitcaster.weatherfit.catalog.domain.repository.ItemRepository;
import com.fitcaster.weatherfit.common.exception.CartItemExistsException;
import com.fitcaster.weatherfit.order.domain.entity.Cart;
import com.fitcaster.weatherfit.order.domain.repository.CartRepository;
import com.fitcaster.weatherfit.user.domain.entity.User;
import com.fitcaster.weatherfit.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * author: 이상우
 * 장바구니 비즈니스 로직을 담당하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository; // 사용자 유효성 검사를 위해 필요
    private final ItemRepository itemRepository; // 상품 유효성 검사를 위해 필요

    /**
     * 장바구니에 상품을 새로 추가합니다. 이미 존재하는 상품이면 예외(CartItemExistsException)를 발생시킵니다.
     * 초기 수량은 항상 1입니다.
     * @param userId 장바구니를 소유한 사용자 ID
     * @param itemId 담을 상품 ID
     * @return 새로 저장된 Cart 엔티티
     * @throws CartItemExistsException 이미 장바구니에 상품이 존재할 경우
     */
    @Transactional
    public Cart addItemToCart(Long userId, Long itemId) {
        final int INITIAL_QUANTITY = 1;

        // User 및 Item 유효성 검사
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. (ID: " + userId + ")"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. (ID: " + itemId + ")"));

        // 이미 장바구니에 담긴 상품인지 확인
        Optional<Cart> existingCartItem = cartRepository.findByUserAndItem(user, item);

        if (existingCartItem.isPresent()) {
            // 이미 존재하면 예외 발생
            throw new CartItemExistsException(
                    "선택하신 상품은 이미 장바구니에 담겨 있습니다. (수량 조절은 장바구니 페이지에서 가능합니다.)",
                    item.getItemName() // 예외를 발생시킨 상품명 전달
            );
        } else {
            // 존재하지 않으면 수량 1로 새로 생성
            Cart newCart = Cart.builder()
                    .user(user)
                    .item(item)
                    .quantity(INITIAL_QUANTITY) // 초기값 1 설정
                    .build();
            return cartRepository.save(newCart);
        }
    }

    /**
     * 특정 사용자의 장바구니 전체 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return 해당 사용자의 장바구니 항목 목록
     */
    public List<Cart> getCartItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. (ID: " + userId + ")"));

        return cartRepository.findByUser(user);
    }

    /**
     * 장바구니 항목의 수량을 변경합니다. (프론트엔드의 수량 조절 버튼 로직)
     * @param cartId 변경할 장바구니 항목 ID
     * @param newQuantity 새로운 수량 (1 이상)
     */
    @Transactional
    public void updateCartItemQuantity(Long cartId, int newQuantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 항목을 찾을 수 없습니다. (ID: " + cartId + ")"));

        // updateQuantity 메서드에서 1 미만일 경우 예외 처리
        cart.updateQuantity(newQuantity);
    }

    /**
     * 장바구니에서 특정 상품을 삭제합니다.
     * @param cartId 삭제할 장바구니 항목 ID
     */
    @Transactional
    public void removeCartItem(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    /**
     * 장바구니에 등록된 상품 개수 조회
     */
    @Transactional
    public int getCartItemCount(Long userId) {
        return cartRepository.countByUserId(userId);
    }
}
