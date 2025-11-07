package com.fitcaster.weatherfit.user.domain.repository;

import com.fitcaster.weatherfit.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일 존재 여부 확인 메서드
    boolean existsByEmail(String email);

    // 이메일로 사용자 조회 메서드
    Optional<User> findByEmail(String email);
}
