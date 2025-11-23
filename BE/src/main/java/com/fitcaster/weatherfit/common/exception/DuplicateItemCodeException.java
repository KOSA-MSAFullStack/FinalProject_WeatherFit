// DuplicateItemCodeException.java
// [상품 코드 중복 예외]
/*
 * 설명: 데이터베이스에 이미 존재하는 상품 코드를 다시 삽입하려고 할 때 발생하는 예외
 *      - ItemService에서 상품 생성 시 사용
 */

package com.fitcaster.weatherfit.common.exception;

public class DuplicateItemCodeException extends RuntimeException {

    // 생성자: 예외 메시지를 받아서 부모 생성자에 전달
    public DuplicateItemCodeException(String message) {
        super(message);
    }
}
