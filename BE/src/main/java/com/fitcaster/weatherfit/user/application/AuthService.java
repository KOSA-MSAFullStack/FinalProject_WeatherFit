package com.fitcaster.weatherfit.user.application;

import com.fitcaster.weatherfit.common.util.JwtTokenProvider;
import com.fitcaster.weatherfit.user.api.dto.request.LoginRequest;
import com.fitcaster.weatherfit.user.api.dto.response.AccessTokenResponse;
import com.fitcaster.weatherfit.user.api.dto.response.LoginResponse;
import com.fitcaster.weatherfit.user.domain.entity.RefreshToken;
import com.fitcaster.weatherfit.user.domain.repository.RefreshTokenRepository;
import com.fitcaster.weatherfit.user.domain.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Optional;

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

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    /**
     * 사용자 로그인을 처리하고 JWT Access Token을 발급
     * @param request 로그인 요청 DTO (이메일, 비밀번호)
     * @param response HttpOnly 쿠키 설정을 위해 사용되는 응답 객체
     * @return Access Token, 만료 시간 정보를 포함하는 응답 DTO
     */
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        // Access Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);

        // Refresh Token 생성 및 DB 저장 (토큰 회전: 기존 토큰 삭제)
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser(user);
        existingTokenOpt.ifPresent(refreshTokenRepository::delete);

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
     * Access Token 재발급 로직
     * HttpOnly 쿠키의 Refresh Token을 검증하여 새 Access Token을 발급합니다.
     */
    @Transactional
    public AccessTokenResponse refreshAccessToken(HttpServletRequest request) {
        // 쿠키에서 Refresh Token 추출
        String refreshTokenValue = extractRefreshTokenFromCookie(request);
        if (refreshTokenValue == null) {
            // 토큰이 없으면 401 처리
            throw new AuthenticationException("Refresh Token 쿠키를 찾을 수 없습니다. (재로그인 필요)") {};
        }

        // Refresh Token 유효성 검증 (JWT 서명, 만료 시간)
        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            // 토큰이 유효하지 않으면 401 처리
            throw new AuthenticationException("만료되었거나 유효하지 않은 Refresh Token입니다. (재로그인 필요)") {};
        }

        // 토큰에서 userId 추출 (JwtTokenProvider에 getUserIdFromToken이 구현되어 있어야 함)
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshTokenValue);

        // DB에서 userId로 Refresh Token 엔티티 조회
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthenticationException("DB에 Refresh Token이 존재하지 않습니다. (로그아웃되었거나 탈취된 토큰)") {});

        // (보안 강화) DB에 저장된 토큰과 쿠키의 토큰이 일치하는지 확인
        if (!refreshToken.getToken().equals(refreshTokenValue)) {
            // 토큰이 일치하지 않으면, 탈취 시도로 간주하고 DB 토큰 삭제 (로그아웃 처리)
            refreshTokenRepository.delete(refreshToken);
            // 비정상 접근으로 401 처리
            throw new AuthenticationException("토큰이 일치하지 않습니다. (비정상 접근)") {};
        }

        // User 엔티티 가져오기
        User user = refreshToken.getUser();
        if (user == null) {
            // 사용자 정보가 없으면 401 처리
            throw new AuthenticationException("Refresh Token에 연결된 사용자를 찾을 수 없습니다.") {};
        }

        // 새로운 Access Token 생성을 위한 Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, // User 객체 (UserDetails 구현체)
                null, // Credentials (비밀번호 필요 없음)
                user.getAuthorities() // 권한
        );

        // 새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        long expiresIn = jwtTokenProvider.getAccessTokenExpiresIn();

        // 응답 DTO 반환
        return AccessTokenResponse.builder()
                .accessToken(newAccessToken)
                .expiresIn(expiresIn)
                .build();
    }

    /**
     * 로그아웃 처리: 쿠키에 저장된 리프레시 토큰을 삭제
     */
    @Transactional
    public void logout(HttpServletResponse response, Long userId) {
        // 서버 측 로직: DB에서 Refresh Token 삭제
        refreshTokenRepository.deleteByUserId(userId);

        // 클라이언트 측 로직: 쿠키 만료시키기
        expireCookie(response, REFRESH_TOKEN_COOKIE_NAME);
    }

    // --- 헬퍼 메서드 ---

    /**
     *  HttpServletRequest에서 Refresh Token 쿠키를 추출합니다.
     */
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 쿠키를 만료(삭제)시키는 헬퍼 메소드
     */
    private void expireCookie(HttpServletResponse response, String cookieName) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(true) // HTTPS 환경이라고 가정
                .sameSite("Strict") // CSRF 방어
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

}
