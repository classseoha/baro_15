package com.example.baro_15.domain.auth.dto.response;

import com.example.baro_15.domain.user.domain.User;
import com.example.baro_15.domain.user.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "유저 권한 변경 응답 DTO")
@Getter
@Builder
@AllArgsConstructor
public class UpdateUserRoleResponseDto {

    @Schema(description = "이메일", example = "admin@example.com")
    private String email;

    @Schema(description = "변경된 권한", example = "ADMIN")
    private UserRole userRole;

    public static UpdateUserRoleResponseDto from(User user) {

        return new UpdateUserRoleResponseDto(user.getEmail(), user.getUserRole());
    }
}
