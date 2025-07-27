package com.example.baro_15.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT 액세스 토큰 응답 DTO")
public record TokenResponseDto(String accessToken) {

}
