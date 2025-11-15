package com.fitcaster.weatherfit.order.api.controller;

import com.fitcaster.weatherfit.order.application.CartService;
import com.fitcaster.weatherfit.order.api.dto.response.CartItemResponse;
import com.fitcaster.weatherfit.order.domain.entity.Cart;
import com.fitcaster.weatherfit.order.api.dto.request.CartAddItemRequest;
import com.fitcaster.weatherfit.order.api.dto.request.CartUpdateQuantityRequest;
import com.fitcaster.weatherfit.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author: 이상우
 * 장바구니 관련 API 엔드포인트를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니에 상품 추가 (POST)
     * 상품 상세 페이지에서 '장바구니 담기' 버튼 클릭 시 사용
     * @param request 상품 ID 정보
     * @return 성공 메시지
     */
    @PostMapping
    public ResponseEntity<Void> addItemToCart(@AuthenticationPrincipal User user, @RequestBody @Valid CartAddItemRequest request) {
        cartService.addItemToCart(user.getId(), request.getItemId());
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created 반환
    }

    /**
     * 장바구니 목록 조회 (GET)
     * 장바구니 페이지 로드 시 사용
     * @return 사용자의 장바구니 항목 목록 DTO
     */
    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems(@AuthenticationPrincipal User user) {
        List<Cart> cartItems = cartService.getCartItems(user.getId());

        List<CartItemResponse> response = cartItems.stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 장바구니 항목 수량 변경 (PUT)
     * 장바구니 페이지에서 수량 +/- 버튼 클릭 시 사용
     * @param cartId 변경할 장바구니 항목 ID
     * @param request 새로운 수량 정보
     * @return 200 OK
     */
    @PutMapping("/{cartId}")
    public ResponseEntity<Void> updateCartItemQuantity(
            @PathVariable Long cartId,
            @RequestBody @Valid CartUpdateQuantityRequest request) {

        cartService.updateCartItemQuantity(cartId, request.getQuantity());
        return ResponseEntity.ok().build();
    }

    /**
     * 장바구니 상품 삭제 (DELETE)
     * 장바구니 페이지에서 '삭제' 버튼 클릭 시 사용
     * @param cartId 삭제할 장바구니 항목 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long cartId) {
        // 실제로는 삭제 요청을 보낸 사용자가 해당 cartId의 소유자인지 확인하는 로직이 필요합니다.
        cartService.removeCartItem(cartId);
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }
}