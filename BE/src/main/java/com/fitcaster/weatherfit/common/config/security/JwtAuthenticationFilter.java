package com.fitcaster.weatherfit.common.config.security;

import com.fitcaster.weatherfit.common.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * author: 이상우
 * 스프링 시큐리티에서 JWT를 이용해 인증을 처리하는 커스텀 필터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 요청 헤더에서 JWT 토큰 추출
        String jwt = resolveToken(request);

        // 토큰 유효성 검사
        if (jwt != null && jwtTokenProvider.validateToken(jwt)) {

            try {
                // 토큰에서 사용자 인증 정보(Authentication) 가져오기
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);

                // SecurityContext에 인증 정보 저장 -> 이 인증 정보로 인해 .anyRequest().authenticated()가 통과됨
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("Security Context에 '{}' 인증 정보를 저장했습니다. URI: {}", authentication.getName(), request.getRequestURI());

            } catch (Exception e) {
                // 유효한 토큰이지만 DB에서 사용자 정보를 찾지 못하는 등 예외 발생 시
                log.error("JWT 인증 처리 중 예외 발생: {}", e.getMessage());
            }
        }

        // 다음 필터로 요청을 전달 (필터 체인의 나머지 단계를 실행)
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청 헤더 (Authorization: Bearer <token>)에서 JWT를 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}