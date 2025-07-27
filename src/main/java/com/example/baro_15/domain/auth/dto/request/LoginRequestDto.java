package com.example.baro_15.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "로그인 요청 DTO")
@Getter
@AllArgsConstructor
public class LoginRequestDto {

    @Schema(description = "이메일", example = "user@example.com")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Schema(description = "비밀번호", example = "test1234!")
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

}
