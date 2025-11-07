package com.fitcaster.weatherfit.user.api.controller;

import com.fitcaster.weatherfit.user.api.dto.request.SignupRequest;
import com.fitcaster.weatherfit.user.application.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user") // 또는 /auth
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest request) {
        // @Valid를 통해 DTO 레벨의 유효성 검증 (Email 형식 등)
        userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created 응답
    }

    // ... 로그인 API는 추후 추가
}