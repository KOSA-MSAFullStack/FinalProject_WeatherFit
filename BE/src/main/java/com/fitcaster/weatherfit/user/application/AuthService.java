package com.fitcaster.weatherfit.user.application;

import com.fitcaster.weatherfit.common.util.JwtTokenProvider;
import com.fitcaster.weatherfit.user.api.dto.request.LoginRequest;
import com.fitcaster.weatherfit.user.api.dto.response.LoginResponse;
import com.fitcaster.weatherfit.user.domain.entity.RefreshToken;
import com.fitcaster.weatherfit.user.domain.repository.RefreshTokenRepository;
import com.fitcaster.weatherfit.user.domain.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fitcaster.weatherfit.user.domain.entity.User;

import java.time.LocalDateTime;

/**
 * author: 이상우
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    // Spring Security의 인증을 담당하는 핵심 컴포넌트
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 사용자 로그인을 처리하고 JWT Access Token을 발급
     * @param request 로그인 요청 DTO (이메일, 비밀번호)
     * @param response HttpOnly 쿠키 설정을 위해 사용되는 응답 객체
     * @return Access Token의 만료 시간 정보를 포함하는 응답 DTO
     */
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {

        // 사용자 인증 시도
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new RuntimeException("로그인 정보가 유효하지 않습니다.", e);
        }

        User user = (User) authentication.getPrincipal();

        // JWT Access Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);

        // 기존 Refresh Token이 있다면 무조건 삭제
        refreshTokenRepository.deleteByUserId(user.getId());

        // Refresh Token을 항상 새로 생성 및 DB에 저장
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(authentication);
        LocalDateTime expiryDate = jwtTokenProvider.getExpirationDate(refreshTokenValue);

        RefreshToken newToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenValue)
                .expiredAt(expiryDate)
                .build();

        user.setRefreshToken(newToken);
        refreshTokenRepository.save(newToken);

        // Refresh Token 쿠키 설정 (HttpOnly, Secure, 긴 만료 시간)
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshTokenValue)
                .httpOnly(true)
                .secure(true)
                .path("/") // 재발급 API 호출을 위해 전역 경로 설정
                .maxAge(jwtTokenProvider.getRefreshTokenExpiresIn())
                .sameSite("Strict") // 더 강력한 CSRF 방어
                .build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        // 응답 DTO 반환 (토큰 값 대신 만료 시간 메타데이터만 포함)
        return LoginResponse.builder()
                .accessToken(accessToken)
                .expiresIn(jwtTokenProvider.getAccessTokenExpiresIn())
                .build();

    }

    /**
     * 로그아웃 처리: 쿠키에 저장된 토큰(Access/Refresh)을 삭제
     */
    @Transactional
    public void logout(HttpServletResponse response, Long userId) {
        // 서버 측 로직: DB에서 Refresh Token 삭제
        refreshTokenRepository.deleteByUserId(userId);

        // 클라이언트 측 로직: 쿠키 만료시키기
        expireCookie(response);
    }

    private void expireCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null); // value는 null로 설정
        cookie.setMaxAge(0); // 만료 시간을 0으로 설정하여 즉시 만료
        cookie.setPath("/"); // 쿠키가 생성된 경로와 동일하게 설정
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

}
