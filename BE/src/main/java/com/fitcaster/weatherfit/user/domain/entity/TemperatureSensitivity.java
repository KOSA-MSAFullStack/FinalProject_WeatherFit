package com.fitcaster.weatherfit.user.domain.entity;

import lombok.Getter;

@Getter
public enum TemperatureSensitivity {
    COLD("추위민감"),
    NORMAL("보통"),
    HOT("더위민감");

    private final String description; // 한글 명칭

    TemperatureSensitivity(String description) {
        this.description = description;
    }

    // String 값을 Enum으로 변환하기 위한 정적 메서드 (회원가입 DTO 처리용)
    public static TemperatureSensitivity fromValue(String value) {
        for (TemperatureSensitivity sensitivity : TemperatureSensitivity.values()) {
            if (sensitivity.name().equalsIgnoreCase(value) || sensitivity.description.equals(value)) {
                return sensitivity;
            }
        }
        // 요청된 문자열이 유효하지 않을 경우 예외 처리
        throw new IllegalArgumentException("Invalid Temperature Sensitivity value: " + value);
    }
}
