package com.fitcaster.weatherfit.user.api.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessTokenResponse {
    private String role;
    private String accessToken;
    private long expiresIn;
}