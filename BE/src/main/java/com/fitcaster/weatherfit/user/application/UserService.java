package com.fitcaster.weatherfit.user.application;

import com.fitcaster.weatherfit.common.exception.DuplicateUserException;
import com.fitcaster.weatherfit.common.exception.InternalServerException;
import com.fitcaster.weatherfit.user.api.dto.request.AddressRequest;
import com.fitcaster.weatherfit.user.api.dto.request.SignupRequest;
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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository; // 💡 Address 저장을 위해 주입
    private final PasswordEncoder passwordEncoder;

    // 회원가입 처리
    @Transactional
    public void signup(SignupRequest request) {
        // 비밀번호 일치 확인 및 중복 확인
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
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
}