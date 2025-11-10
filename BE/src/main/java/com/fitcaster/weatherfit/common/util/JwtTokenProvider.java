package com.fitcaster.weatherfit.common.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * author: 이상우
 * JWT(JSON Web Token)를 기반으로 토큰을 생성, 추출, 검증하는 등 JWT와 관련된 모든 작업을 처리하는 클래스
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    // application.yaml에서 주입받을 값
    @Value("${jwt.secret}")
    private String secretKeyString;
    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;
    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    // 시크릿 키를 안전하게 저장할 객체
    private SecretKey secretKey;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // 객체 생성 후 secretKeyString을 기반으로 SecretKey를 초기화
    @PostConstruct
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 생성 메서드
    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, accessTokenExpirationMs, false);
    }

    // Refresh Token 생성 메서드
    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenExpirationMs, true);
    }

    /**
     * JWT 생성을 위한 private 헬퍼 메서드
     * @param authentication 인증 정보
     * @param expirationMs 만료 시간 (밀리초)
     * @return 생성된 JWT 문자열
     */
    private String createToken(Authentication authentication, long expirationMs, boolean isRefreshToken) {
        String userEmail = authentication.getName();

        // Claims를 Map으로 대체하여 초기화 (subject를 Map에 직접 추가)
        Map<String, Object> claims = new HashMap<>();
        claims.put(Claims.SUBJECT, userEmail);

        if (isRefreshToken) {
            // Refresh Token 클레임 설정: 'roles' 제외, 'token_type' 명시
            claims.put("token_type", "refresh");
        } else {
            // Access Token 클레임 설정: 'roles' 포함, 'token_type' 명시
            String authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            claims.put("roles", authorities);
            claims.put("token_type", "access");
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now) // 토큰 발급 시간
                .expiration(expiryDate) // 토큰 만료 시간
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    // Access Token의 만료 시간(초 단위)을 제공하는 메서드 추가
    public long getAccessTokenExpiresIn() {
        return accessTokenExpirationMs / 1000;
    }

    /**
     * JWT에서 만료 시점(LocalDateTime)을 추출
     * @param token JWT (Refresh Token)
     * @return 만료 시점 LocalDateTime
     */
    public LocalDateTime getExpirationDate(String token) {
        // 토큰 파싱
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // java.util.Date를 LocalDateTime으로 변환
        return claims.getExpiration().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * JWT에서 사용자 인증 정보를 추출
     * @param token JWT
     * @return 인증 객체 (UsernamePasswordAuthenticationToken)
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String userEmail = claims.getSubject();

        // email로 UserDetails 객체를 로드 (DB 조회)
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        // Security Context에 저장할 인증 객체 생성
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * JWT에서 Claims (payload) 정보를 추출
     * @param token JWT
     * @return Claims 객체
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 토큰의 유효성을 검증
     * @param token JWT
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}