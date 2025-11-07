package com.fitcaster.weatherfit.common.exception;

public class InternalServerException extends RuntimeException {

    // 메시지만 받는 생성자 (일반적인 오류 발생 시)
    public InternalServerException(String message) {
        super(message);
    }

    // 메시지와 원인(Throwable)을 함께 받는 생성자 (UserService에서 사용)
    // 원본 예외(e)의 스택 트레이스를 보존하여 디버깅을 돕습니다.
    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

}
