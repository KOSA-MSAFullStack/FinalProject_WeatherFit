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

    /**
     * Access Token과 Refresh Token을 동시에 생성합니다.
     * @param authentication 인증된 사용자 정보
     * @return Access Token과 Refresh Token을 포함하는 맵
     */
    public Map<String, String> createToken(Authentication authentication) {
        String userEmail = authentication.getName();
        // authorities: ROLE_USER 추출
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        // Access Token 생성
        Date accessTokenExpiryDate = new Date(now.getTime() + accessTokenExpirationMs);
        String accessToken = Jwts.builder()
                .subject(userEmail)
                .claim("roles", authorities)
                .issuedAt(now)
                .expiration(accessTokenExpiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        // Refresh Token 생성
        Date refreshTokenExpiryDate = new Date(now.getTime() + refreshTokenExpirationMs);
        String refreshToken = Jwts.builder()
                .subject(userEmail)
                .issuedAt(now)
                .expiration(refreshTokenExpiryDate)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        // 맵에 담아 반환
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    // Access Token의 만료 시간(초 단위)을 제공하는 메서드 추가
    public long getAccessTokenExpiresIn() {
        return accessTokenExpirationMs / 1000;
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