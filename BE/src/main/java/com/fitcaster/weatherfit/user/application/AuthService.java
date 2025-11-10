package com.fitcaster.weatherfit.user.application;

import com.fitcaster.weatherfit.common.util.JwtTokenProvider;
import com.fitcaster.weatherfit.user.api.dto.request.LoginRequest;
import com.fitcaster.weatherfit.user.api.dto.response.LoginResponse;
import com.fitcaster.weatherfit.user.domain.entity.RefreshToken;
import com.fitcaster.weatherfit.user.domain.repository.RefreshTokenRepository;
import com.fitcaster.weatherfit.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fitcaster.weatherfit.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * @return JWT Access Token을 포함하는 응답 DTO
     */
    public LoginResponse login(LoginRequest request) {

        // 인증 객체 생성 (사용자 입력 정보)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );

        Authentication authentication;
        try {
            // AuthenticationManager를 통해 인증 시도
            // UserDetailsService의 loadUserByUsername() 호출 및 비밀번호 비교를 자동으로 수행
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            // 인증 실패 (비밀번호 불일치, 사용자 없음 등) 시 예외 처리
            throw new RuntimeException("로그인 정보가 유효하지 않습니다.", e);
        }

        User user = (User) authentication.getPrincipal();

        // JWT Access 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);

        // 기존 Refresh Token이 있다면 무조건 삭제
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser(user);
        existingTokenOpt.ifPresent(refreshTokenRepository::delete);

        // 로그인 시 Refresh Token을 항상 새로 생성
        String refreshTokenValue = jwtTokenProvider.createRefreshToken(authentication);
        LocalDateTime expiryDate = jwtTokenProvider.getExpirationDate(refreshTokenValue);

        // DB에 새 엔티티 저장 (Create or Overwrite)
        RefreshToken newToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenValue)
                .expiredAt(expiryDate)
                .build();

        user.setRefreshToken(newToken); // 양방향 관계 설정
        refreshTokenRepository.save(newToken);

        // 응답 DTO 반환
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .expiresIn(jwtTokenProvider.getAccessTokenExpiresIn()) // Access Token 초 단위 만료 시간
                .build();

    }
}
