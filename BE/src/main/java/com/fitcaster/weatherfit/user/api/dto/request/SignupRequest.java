package com.fitcaster.weatherfit.user.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignupRequest {
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 8자 이상이며, 영문, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "연락처는 필수입니다.")
    private String phone;

    @NotBlank(message = "성별은 필수입니다.")
    private String gender;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일은 YYYY-MM-DD 형식이어야 합니다.")
    private String birth;

    @NotBlank(message = "날씨 민감도는 필수입니다.")
    private String temperatureSensitivity;

    // 하위 주소 객체에 대한 유효성 검증을 위해 @Valid, @NotNull을 사용
    @Valid
    @NotNull(message = "주소 정보는 필수입니다.")
    private AddressRequest address;
}
