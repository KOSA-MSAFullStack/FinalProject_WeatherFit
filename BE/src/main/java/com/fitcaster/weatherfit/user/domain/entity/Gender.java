package com.fitcaster.weatherfit.user.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE(0, "M", "남성"),
    FEMALE(1, "F", "여성");

    private final int code;
    private final String value;
    private final String description;

    public static Gender fromValue(String value) {
        for (Gender g : Gender.values()) {
            if (g.value.equalsIgnoreCase(value)) {
                return g;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 성별 값입니다: " + value);
    }
}
