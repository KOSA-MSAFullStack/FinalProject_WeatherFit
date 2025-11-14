package com.fitcaster.weatherfit.user.application;

import com.fitcaster.weatherfit.common.exception.DuplicateUserException;
import com.fitcaster.weatherfit.common.exception.InternalServerException;
import com.fitcaster.weatherfit.user.api.dto.request.AddressRequest;
import com.fitcaster.weatherfit.user.api.dto.request.ProfileUpdateRequest;
import com.fitcaster.weatherfit.user.api.dto.request.SignupRequest;
import com.fitcaster.weatherfit.user.api.dto.response.ProfileResponse;
import com.fitcaster.weatherfit.user.domain.entity.Address;
import com.fitcaster.weatherfit.user.domain.entity.Gender;
import com.fitcaster.weatherfit.user.domain.entity.TemperatureSensitivity;
import com.fitcaster.weatherfit.user.domain.entity.User;
import com.fitcaster.weatherfit.user.domain.repository.AddressRepository;
import com.fitcaster.weatherfit.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * author: 이상우
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    // 이메일 중복 확인
    public boolean checkEmailDuplication(String email) {
        return !userRepository.existsByEmail(email);
    }

    // 회원가입 처리
    @Transactional
    public void signup(SignupRequest request) {
        // 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("이미 가입된 이메일입니다.");
        }

        // 입력 데이터 변환 (Enum, Date)
        Gender userGender;
        LocalDate birth;
        TemperatureSensitivity sensitivity;

        try {
            // String -> Gender Enum 변환 ("F" -> Gender.FEMALE)
            userGender = Gender.fromValue(request.getGender());
            // String -> LocalDate 변환 ("YYYY-MM-DD")
            birth = LocalDate.parse(request.getBirth());
            // String -> TemperatureSensitivity Enum 변환
            sensitivity = TemperatureSensitivity.fromValue(request.getTemperatureSensitivity());
        } catch (IllegalArgumentException | DateTimeParseException e) {
            // Enum 변환 또는 날짜 파싱 실패 시 예외 처리
            throw new InternalServerException("잘못된 입력 데이터 형식입니다.", e);
        }

        // 비밀번호 암호화 및 User 엔티티 생성
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .phone(request.getPhone())
                .gender(userGender)
                .birth(birth)
                .temperatureSensitivity(sensitivity)
                .role("ROLE_USER")
                .build();

        User savedUser = userRepository.save(newUser); // User 저장

        // Address 엔티티 생성 및 저장 (1:N 관계 처리)
        AddressRequest addressReq = request.getAddress();
        Address newAddress = Address.builder()
                .user(savedUser)
                .zipCode(addressReq.getZipCode())
                .baseAddress(addressReq.getBase())
                .detailAddress(addressReq.getDetail())
                .build();

        // User 엔티티의 @OneToMany 필드에도 추가 (양방향 매핑)
        savedUser.addAddress(newAddress);

        // Address Repository를 통해 저장
        addressRepository.save(newAddress);
    }

    // 사용자 프로필 정보 조회
    @Transactional
    public ProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        return ProfileResponse.from(user);
    }

    // 사용자 프로필 정보 업데이트
    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        user.updateProfile(request);
    }
}