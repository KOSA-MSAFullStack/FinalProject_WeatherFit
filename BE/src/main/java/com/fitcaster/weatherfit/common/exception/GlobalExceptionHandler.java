// GlobalExceptionHandler.java
// 전역 예외 처리 핸들러

package com.fitcaster.weatherfit.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // [상품 코드 중복과 같은 잘못된 인자 예외 처리]
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT); // 409 Conflict
    }

    // [서버 내부 로직 오류 예외 처리]
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleInternalServerException(InternalServerException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
    }

    // [처리되지 않은 모든 예외에 대한 최종 처리]
    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "⚠️ 알 수 없는 오류 발생!");
        // log.error("Unhandled exception:", e); // 로깅 추가 가능
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
    }
}
