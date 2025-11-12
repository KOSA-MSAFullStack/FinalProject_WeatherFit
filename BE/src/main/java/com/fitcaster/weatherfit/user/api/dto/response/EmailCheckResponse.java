package com.fitcaster.weatherfit.user.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailCheckResponse {

    private final String email;
    private final boolean isAvailable; // true: 사용 가능, false: 사용 중
    private final String message;      // 사용자에게 보여줄 메시지

}