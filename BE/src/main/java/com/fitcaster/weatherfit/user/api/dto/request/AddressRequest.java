package com.fitcaster.weatherfit.user.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank(message = "우편번호는 필수입니다.")
    private String zipCode;

    @NotBlank(message = "기본 주소는 필수입니다.")
    private String base;

    @NotBlank(message = "상세 주소는 필수입니다.")
    private String detail;
}
