package com.fitcaster.weatherfit.mypage.api.controller;

import com.fitcaster.weatherfit.mypage.application.ProfileService;
import com.fitcaster.weatherfit.mypage.api.dto.request.ProfileUpdateRequest;
import com.fitcaster.weatherfit.mypage.api.dto.response.ProfileResponse;
import com.fitcaster.weatherfit.order.application.CartService;
import com.fitcaster.weatherfit.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * author: 이상우
 */
@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final ProfileService profileService;
    private final CartService cartService;

    /**
     * GET /mypage/profile (user profile 조회)
     */
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ProfileResponse profile = profileService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /mypage/profile (user profile 수정)
     */
    @PutMapping("/profile")
    public ResponseEntity<Void> updateUserProfile(@AuthenticationPrincipal User user, @RequestBody ProfileUpdateRequest request) {
        Long userId = user.getId();
        profileService.updateProfile(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /mypage/cart (장바구니 상품 개수 조회)
     */
    @GetMapping("/cart")
    public ResponseEntity<Integer> getCartItemCount(@AuthenticationPrincipal User user) {
        Long userId = user.getId();
        int count = cartService. getCartItemCount(userId);
        return ResponseEntity.ok(count);
    }
}
