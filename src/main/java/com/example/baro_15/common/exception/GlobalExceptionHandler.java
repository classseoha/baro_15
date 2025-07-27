package com.example.baro_15.common.exception;

import com.example.baro_15.common.exception.dto.ErrorReasonDto;
import com.example.baro_15.common.exception.dto.FieldErrorDetail;
import com.example.baro_15.common.response.ApiResponse;
import com.example.baro_15.common.status.ErrorStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<ErrorReasonDto>> handleApiException(ApiException e) {

        return ApiResponse.onFailure(e.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<FieldErrorDetail>>> handleValidationException(MethodArgumentNotValidException e) {

        List<FieldErrorDetail> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> FieldErrorDetail.of(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                ))
                .toList();

        ErrorReasonDto reason = ErrorStatus.INVALID_REQUEST.getReasonHttpStatus();

        // 실패 응답: ErrorStatus.INVALID_REQUEST 사용
        return ResponseEntity.status(reason.getHttpStatus())
                .body(new ApiResponse<>(
                        false,
                        reason.getCode(),
                        reason.getMessage(),
                        fieldErrors
                ));
    }

    // 추가로, 예기치 않은 모든 예외를 처리하는 fallback 핸들러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorReasonDto>> handleUnhandledException(Exception e) {

        e.printStackTrace();  // 서버 로그 기록용

        return ApiResponse.onFailure(ErrorStatus.INTERNAL_SERVER_ERROR);
    }
}
