package com.fitcaster.weatherfit.user.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * author: 이상우
 * 로그인 응답 DTO (리프레시 토큰 값은 HttpOnly 쿠키로 전송)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String role;
    private String accessToken;
    private long expiresIn; // Access Token 만료 시간 (초 단위)
}
