package com.fitcaster.weatherfit.user.api.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * author: 이상우
 */
@Getter
@Builder
public class LoginResponse {

    private final String tokenType = "Bearer";
    private String accessToken;
    private String refreshToken;
    private long expiresIn; // Access Token 만료 시간 (초 단위)
}
