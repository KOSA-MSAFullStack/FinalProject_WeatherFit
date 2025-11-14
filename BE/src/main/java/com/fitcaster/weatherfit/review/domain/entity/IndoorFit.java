package com.fitcaster.weatherfit.review.domain.entity;

import lombok.Getter;

@Getter
public enum IndoorFit {
    COMFORTABLE("편해요"), NORMAL("보통이에요"), TIGHT("답답해요");
    private final String description;
    IndoorFit(String description) { this.description = description; }
}
