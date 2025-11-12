package com.fitcaster.weatherfit.user.api.controller;

import com.fitcaster.weatherfit.user.api.dto.request.LoginRequest;
import com.fitcaster.weatherfit.user.api.dto.request.SignupRequest;
import com.fitcaster.weatherfit.user.api.dto.response.EmailCheckResponse;
import com.fitcaster.weatherfit.user.api.dto.response.LoginResponse;
import com.fitcaster.weatherfit.user.application.AuthService;
import com.fitcaster.weatherfit.user.application.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users") // 또는 /auth
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * GET /users/checkEmail (이메일 중복 확인)
     * @param email 확인할 이메일 주소
     * @return 이메일 사용 가능 여부를 포함하는 응답 DTO
     */
    @GetMapping("/checkEmail")
    public ResponseEntity<EmailCheckResponse> checkEmailDuplication(@RequestParam("email") String email) {

        // 서비스 호출하여 중복 여부 확인
        boolean isAvailable = userService.checkEmailDuplication(email);

        // 응답 DTO 생성
        EmailCheckResponse response = EmailCheckResponse.builder()
                .email(email)
                .isAvailable(isAvailable) // 사용 가능 여부
                .message(isAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.")
                .build();

        // 200 OK 상태와 함께 응답
        return ResponseEntity.ok(response);
    }

    /**
     * POST /users/signup (회원가입)
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest request) {
        // @Valid를 통해 DTO 레벨의 유효성 검증 (Email 형식 등)
        userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created 응답
    }

    /**
     * POST /users/login (로그인)
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request, response);
        return ResponseEntity.ok(loginResponse);
    }
}