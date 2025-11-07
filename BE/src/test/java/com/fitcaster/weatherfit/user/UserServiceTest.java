package com.fitcaster.weatherfit.user;

import com.fitcaster.weatherfit.common.config.security.SecurityConfig;
import com.fitcaster.weatherfit.common.exception.DuplicateUserException;
import com.fitcaster.weatherfit.common.exception.InternalServerException;
import com.fitcaster.weatherfit.user.api.dto.request.AddressRequest;
import com.fitcaster.weatherfit.user.api.dto.request.SignupRequest;
import com.fitcaster.weatherfit.user.application.UserService;
import com.fitcaster.weatherfit.user.domain.repository.AddressRepository;
import com.fitcaster.weatherfit.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Import(SecurityConfig.class) // PasswordEncoder Bean을 로드하기 위해 SecurityConfig 임포트
@Transactional // 테스트 후 롤백 (DB에 영향 X)
class UserServiceTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private AddressRepository addressRepository;

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
}
