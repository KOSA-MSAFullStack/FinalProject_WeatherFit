package com.fitcaster.weatherfit.mypage.application;

import com.fitcaster.weatherfit.mypage.api.dto.request.ProfileUpdateRequest;
import com.fitcaster.weatherfit.mypage.api.dto.response.ProfileResponse;
import com.fitcaster.weatherfit.user.domain.entity.User;
import com.fitcaster.weatherfit.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    // 사용자 프로필 정보 조회
    @Transactional
    public ProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        return ProfileResponse.from(user);
    }

    // 사용자 프로필 정보 업데이트
    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        user.updateProfile(request);
    }
}
