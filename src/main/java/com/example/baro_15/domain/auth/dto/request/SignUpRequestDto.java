package com.example.baro_15.domain.auth.dto.request;

import com.example.baro_15.domain.user.domain.UserRole;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "회원가입 요청 DTO")
@Getter
@AllArgsConstructor
public class SignUpRequestDto {

    @Schema(description = "이메일", example = "user@example.com")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Schema(description = "비밀번호", example = "test1234!")
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자리 이상이어야 합니다.")
    @Pattern(regexp = "(?=.*[A-Za-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}", message = "비밀번호는 영문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

}
