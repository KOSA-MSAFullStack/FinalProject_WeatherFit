package com.fitcaster.weatherfit.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "zip_code", length = 20, nullable = false)
    private String zipCode; // 우편번호

    @Column(name = "base_address", length = 255, nullable = false)
    private String baseAddress; // 기본 주소

    @Column(name = "detail_address", length = 255, nullable = false)
    private String detailAddress; // 상세 주소

    @Builder
    public Address(User user, String zipCode, String baseAddress, String detailAddress) {
        this.user = user;
        this.zipCode = zipCode;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
