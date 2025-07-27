package com.example.baro_15.common.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "에러 응답 DTO")
public class ErrorReasonDto {

    @Schema(description = "성공 여부", example = "false")
    private final boolean isSuccess;

    @Schema(description = "HTTP 상태 코드", example = "404")
    private final HttpStatus httpStatus;

    @Schema(description = "에러 코드", example = "USER_NOT_FOUND")
    private final String code;

    @Schema(description = "에러 메시지", example = "해당 사용자를 찾을 수 없습니다.")
    private final String message;

    // Optional: validation error인 경우만 사용
    private final List<FieldErrorDetail> fieldErrors;

}
