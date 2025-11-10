package com.fitcaster.weatherfit.common.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * author: 이상우
 * 인증되지 않은 사용자가 보호된 리소스에 접근하려 할 때 발생하는 예외를 처리하는 Spring Security의 구현체
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 유효한 자격증명을 제공하지 않고 접근하려 할 때 401 Unauthorized 에러를 리턴합니다.
        log.warn("Unauthorized access error. Request URI: {}", request.getRequestURI());

        // 로그인 실패 시에도 이 핸들러가 호출되어 401을 명확히 반환하게 됩니다.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
