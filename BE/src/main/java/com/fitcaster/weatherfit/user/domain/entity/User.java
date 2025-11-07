package com.fitcaster.weatherfit.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    // PK 설정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password; // 암호화된 비밀번호
    @Column(nullable = false)
    private String name;


    @Column(name = "phone_number", nullable = false, length = 50)
    private String phone;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "gender", nullable = false, length = 1)
    private Gender gender;
    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @Enumerated(EnumType.STRING) // DB에 Enum의 이름(COLD, NORMAL, HOT)이 문자열로 저장되도록 설정
    @Column(name = "temperature_sensitivity", nullable = false, length = 20)
    private TemperatureSensitivity temperatureSensitivity;
    @Column(name = "role", nullable = false, length = 20)
    private String role;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Address 엔티티와 일대다 관계 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @Builder
    public User(String email, String password, String name, String phone,
                Gender gender, LocalDate birth, TemperatureSensitivity temperatureSensitivity,
                String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.birth = birth;
        this.temperatureSensitivity = temperatureSensitivity;
        this.role = role;
        this.createdAt = LocalDateTime.now(); // 가입일 자동 설정
    }

    public void addAddress(Address address) {
        this.addresses.add(address);

        if (address.getUser() != this) {
            address.setUser(this);
        }
    }
}