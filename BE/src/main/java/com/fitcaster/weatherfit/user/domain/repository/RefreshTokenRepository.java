package com.fitcaster.weatherfit.user.domain.repository;

import com.fitcaster.weatherfit.user.domain.entity.RefreshToken;
import com.fitcaster.weatherfit.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser(User user);
}