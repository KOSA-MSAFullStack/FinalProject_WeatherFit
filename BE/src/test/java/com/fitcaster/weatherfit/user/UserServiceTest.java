package com.fitcaster.weatherfit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitcaster.weatherfit.common.config.security.SecurityConfig;
import com.fitcaster.weatherfit.common.exception.DuplicateUserException;
import com.fitcaster.weatherfit.common.exception.InternalServerException;
import com.fitcaster.weatherfit.user.api.dto.request.AddressRequest;
import com.fitcaster.weatherfit.user.api.dto.request.LoginRequest;
import com.fitcaster.weatherfit.user.api.dto.request.SignupRequest;
import com.fitcaster.weatherfit.user.api.dto.response.LoginResponse;
import com.fitcaster.weatherfit.user.application.UserService;
import com.fitcaster.weatherfit.user.domain.repository.AddressRepository;
import com.fitcaster.weatherfit.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class) // PasswordEncoder Bean을 로드하기 위해 SecurityConfig 임포트
@Transactional // 테스트 후 롤백 (DB에 영향 X)
class UserServiceTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private AddressRepository addressRepository;

    @Autowired private MockMvc mockMvc; // 💡 MockMvc 주입
    @Autowired private ObjectMapper objectMapper;

    private SignupRequest createDefaultSignupRequest() {
        // SignupRequest DTO 구조에 맞춰 데이터를 준비
        SignupRequest request = new SignupRequest();
        request.setEmail("test@fitcaster.com");
        request.setPassword("Password!123");
        request.setPasswordConfirm("Password!123");
        request.setName("테스트사용자");
        request.setPhone("01011112222");
        request.setGender("M"); // Gender Enum에 맞게 M 또는 F
        request.setBirth("1990-01-01");
        request.setTemperatureSensitivity("NORMAL");

        AddressRequest addressRequest = new AddressRequest();
        addressRequest.setZipCode("12345");
        addressRequest.setBase("서울시 강남구");
        addressRequest.setDetail("101호");
        request.setAddress(addressRequest);

        return request;
    }

    // --- 로그인 요청 DTO 생성 메서드 ---
    private LoginRequest createLoginRequest(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    // --- 쿠키 값 추출 헬퍼 ---
    private String extractTokenFromCookie(String cookieHeader, String tokenName) {
        // Set-Cookie 헤더에서 특정 토큰 이름에 해당하는 값을 추출하는 정규식
        Pattern pattern = Pattern.compile(tokenName + "=([^;]+)");
        Matcher matcher = pattern.matcher(cookieHeader);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalStateException("응답 헤더에서 " + tokenName + " 쿠키를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("회원가입에 성공하면 User와 Address가 DB에 저장되어야 한다")
    void signup_Success_SavesUserAndAddress() {
        // given
        SignupRequest request = createDefaultSignupRequest();

        // when
        userService.signup(request);

        // then
        // User 저장 확인
        assertThat(userRepository.count()).isEqualTo(1);

        // Address 저장 확인 (1:1 관계이므로 count=1)
        assertThat(addressRepository.count()).isEqualTo(1);

        // 데이터 검증 (필수 필드 및 연관관계)
        var savedUser = userRepository.findByEmail(request.getEmail()).orElseThrow();
        assertThat(savedUser.getName()).isEqualTo("테스트사용자");
        // 비밀번호가 암호화되었는지 확인
        assertThat(savedUser.getPassword()).isNotEqualTo(request.getPassword());

        // 양방향 연관관계 및 데이터 일치 확인
        assertThat(savedUser.getAddresses()).hasSize(1);
        assertThat(savedUser.getAddresses().get(0).getZipCode()).isEqualTo("12345");

        // Address의 FK (User) 설정 확인
        assertThat(addressRepository.findAll().get(0).getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 시 DuplicateUserException이 발생해야 한다")
    void signup_Fail_ThrowsDuplicateUserException() {
        // given
        SignupRequest request = createDefaultSignupRequest();
        // 첫 번째 회원가입 성공
        userService.signup(request);

        // when & then
        // 동일한 이메일로 다시 시도하면 예외 발생 예상
        assertThrows(DuplicateUserException.class, () -> {
            userService.signup(request);
        });

        // 데이터가 중복으로 저장되지 않았는지 확인
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("비밀번호 불일치 시 IllegalArgumentException이 발생해야 한다")
    void signup_Fail_ThrowsIllegalArgumentException() {
        // given
        SignupRequest request = createDefaultSignupRequest();
        request.setPasswordConfirm("WrongPassword!123"); // 비밀번호 불일치

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.signup(request);
        });
    }

    @Test
    @DisplayName("잘못된 날짜 형식 입력 시 InternalServerException이 발생해야 한다")
    void signup_Fail_ThrowsInternalServerExceptionForInvalidDate() {
        // given
        SignupRequest request = createDefaultSignupRequest();
        request.setBirth("1990/01/01"); // 잘못된 형식

        // when & then
        assertThrows(InternalServerException.class, () -> {
            userService.signup(request);
        });
    }

    @Test
    @DisplayName("로그인 성공 시, JWT 토큰이 HttpOnly 쿠키로 반환되어야 한다")
    void login_Success_ReturnsJwtTokensAsHttpOnlyCookies() throws Exception {
        // given
        // 테스트 사용자 사전 등록 (DB에 저장)
        SignupRequest signupRequest = createDefaultSignupRequest();
        userService.signup(signupRequest);

        // 로그인 요청 객체 준비 (이메일: test@fitcaster.com, 비밀번호: Password!123)
        LoginRequest loginRequest = createLoginRequest(
                signupRequest.getEmail(),
                signupRequest.getPassword()
        );

        // when
        // API 엔드포인트: POST /users/login
        ResultActions result = mockMvc.perform(
                post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
        ).andDo(print()); // 요청 및 응답 로그 출력

        // then
        // HTTP 상태 코드 200 OK 검증 및 응답 본문 검증 (토큰 값 대신 expiresIn만 존재하는지 확인)
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.expiresIn").isNumber()) // Access Token 만료 시간(초)이 숫자 형식인지 확인
                .andExpect(jsonPath("$.accessToken").doesNotExist()) // Access Token 필드가 응답 본문에 없어야 함
                .andExpect(jsonPath("$.refreshToken").doesNotExist()); // Refresh Token 필드가 응답 본문에 없어야 함

        // 응답 헤더에서 'Set-Cookie' 검증 (가장 중요)
        String setCookieHeader = result.andReturn().getResponse().getHeader("Set-Cookie");

        // Access Token 쿠키 검증 (HttpOnly, Secure, SameSite=Lax 속성 확인)
        String expectedAccessCookieRegex = "accessToken=([A-Za-z0-9-_\\.]+); Path=/; Max-Age=\\d+; Expires=.*?; Secure; HttpOnly; SameSite=Lax";
        assertThat(setCookieHeader).containsPattern(expectedAccessCookieRegex);

        // Refresh Token 쿠키 검증 (HttpOnly, Secure, SameSite=Strict 속성 확인)
        String expectedRefreshCookieRegex = "refreshToken=([A-Za-z0-9-_\\.]+); Path=/; Max-Age=\\d+; Expires=.*?; Secure; HttpOnly; SameSite=Strict";
        assertThat(setCookieHeader).containsPattern(expectedRefreshCookieRegex);

        // 쿠키에서 토큰 값 추출 및 JWT 형식 검증
        String accessToken = extractTokenFromCookie(setCookieHeader, "accessToken");
        String refreshToken = extractTokenFromCookie(setCookieHeader, "refreshToken");

        // 토큰이 Base64로 인코딩된 JWT 형식 (헤더.페이로드.서명)인지 확인
        assertThat(accessToken.split("\\.").length).isEqualTo(3);
        assertThat(refreshToken.split("\\.").length).isEqualTo(3);
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시도 시 401 Unauthorized가 발생해야 한다")
    void login_Fail_ThrowsUnauthorizedForWrongPassword() throws Exception {
        // given
        // 테스트 사용자 사전 등록
        userService.signup(createDefaultSignupRequest());

        // 잘못된 비밀번호 요청 객체 준비
        LoginRequest loginRequest = createLoginRequest(
                "test@fitcaster.com",
                "WrongPassword!123" // 잘못된 비밀번호
        );

        // when & then
        // API 엔드포인트: POST /users/login
        mockMvc.perform(
                        post("/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andDo(print())
                // 인증 실패 시 Spring Security가 반환하는 상태 코드 (401) 검증
                .andExpect(status().isUnauthorized());
    }
}
