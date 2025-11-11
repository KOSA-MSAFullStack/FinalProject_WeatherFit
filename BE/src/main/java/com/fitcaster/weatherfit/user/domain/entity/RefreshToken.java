package com.fitcaster.weatherfit.user.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "token", nullable = false, length = 512)
    private String token;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Builder
    public RefreshToken(User user, String token, LocalDateTime expiredAt) {
        this.user = user;
        this.token = token;
        this.createdAt = LocalDateTime.now();
        this.expiredAt = expiredAt;
    }

    // 토큰 값 업데이트 메서드 (토큰 재발급 시 사용)
    public void updateToken(String newToken, LocalDateTime newExpiredAt) {
        this.token = newToken;
        this.expiredAt = newExpiredAt;
        this.createdAt = LocalDateTime.now();
    }

    public void setUser(User user) {
        this.user = user;
    }
}
