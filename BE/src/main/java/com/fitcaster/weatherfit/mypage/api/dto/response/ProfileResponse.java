package com.fitcaster.weatherfit.mypage.api.dto.response;

import com.fitcaster.weatherfit.user.domain.entity.Address;
import com.fitcaster.weatherfit.user.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

/**
 * author: 이상우
 * myPage 기본 정보 응답 DTO
 */
@Getter
@Builder
public class ProfileResponse {

    private final String name;
    private final String email;
    private final String birth; // LocalDate -> String
    private final String gender; // Gender Enum -> String
    private final String phone;
    private final String zipCode; // Address 객체에서 추출
    private final String baseAddress; // Address 객체에서 추출
    private final String detailAddress; // Address 객체에서 추출
    private final String temperatureSensitivity; // TemperatureSensitivity Enum -> String

    /**
     * User 엔티티를 ProfileResponse DTO로 변환하는 정적 팩토리 메소드.
     * @param user 변환할 User 엔티티 객체
     * @return 변환된 ProfileResponse DTO 객체
     */
    public static ProfileResponse from(User user) {
        // 사용자는 항상 주소를 가지므로, 리스트의 첫 번째 주소를 바로 사용
        Address primaryAddress = user.getAddresses().get(0);

        return ProfileResponse.builder()
                .name(user.getName())
                .email(user.getUsername())
                .birth(user.getBirth().toString())
                .gender(user.getGender().name())
                .phone(user.getPhone())
                .temperatureSensitivity(user.getTemperatureSensitivity().name())
                // Address 객체에서 직접 정보를 추출
                .zipCode(primaryAddress.getZipCode())
                .baseAddress(primaryAddress.getBaseAddress())
                .detailAddress(primaryAddress.getDetailAddress())
                .build();
    }
}