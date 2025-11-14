package com.fitcaster.weatherfit.user.api.dto.request;

import com.fitcaster.weatherfit.user.domain.entity.TemperatureSensitivity;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequest {
    private String name;
    private LocalDate birth;
    private String gender;
    private String phone;
    private TemperatureSensitivity temperatureSensitivity;
    private AddressRequest address;
}
