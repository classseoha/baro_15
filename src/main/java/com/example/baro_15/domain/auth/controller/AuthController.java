package com.example.baro_15.domain.auth.controller;

import com.example.baro_15.common.exception.dto.ErrorReasonDto;
import com.example.baro_15.common.response.ApiResponse;
import com.example.baro_15.common.security.jwt.JwtUtils;
import com.example.baro_15.common.status.SuccessStatus;
import com.example.baro_15.domain.auth.dto.request.LoginRequestDto;
import com.example.baro_15.domain.auth.dto.request.SignUpRequestDto;
import com.example.baro_15.domain.auth.dto.response.TokenResponseDto;
import com.example.baro_15.domain.auth.dto.response.UpdateUserRoleResponseDto;
import com.example.baro_15.domain.auth.service.AuthService;
import com.example.baro_15.domain.auth.token.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증/인가 API", description = "회원가입, 로그인, 로그아웃, 권한 부여 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final JwtUtils jwtUtils;

    @Operation(summary = "회원가입", description = "새로운 유저를 생성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입에 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = TokenResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 존재하는 이메일입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 형식이 올바르지 않음 (유효성 검증 실패)",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDto.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponseDto>> signup(
            @Valid
            @RequestBody SignUpRequestDto requestDto, ServletRequest servletRequest) {

        TokenResponseDto token = authService.signup(requestDto);

        return ApiResponse.onSuccess(SuccessStatus.SIGNUP_SUCCESS, null);
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 AccessToken을 발급받습니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인에 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = TokenResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호가 잘못되었습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDto.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDto>> login(
            @Valid
            @RequestBody LoginRequestDto requestDto, ServletRequest servletRequest) {

        TokenResponseDto tokens = authService.login(requestDto);

        return ApiResponse.onSuccess(SuccessStatus.LOGIN_SUCCESS, tokens);
    }

    @Operation(summary = "로그아웃", description = "AccessToken을 블랙리스트에 등록하여 로그아웃 처리합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃에 성공했습니다.",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 액세스 토큰입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDto.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(ServletRequest servletRequest) {

        String token = jwtUtils.resolveToken(servletRequest);
        authService.logout(token);

        return ApiResponse.onSuccess(SuccessStatus.LOGOUT_SUCCESS, null);
    }

    @Operation(summary = "관리자 권한 부여", description = "해당 유저에게 ADMIN 권한을 부여합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "ADMIN 권한을 부여받았습니다.",
                    content = @Content(schema = @Schema(implementation = UpdateUserRoleResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDto.class)))
    })
    @PatchMapping("/admin/users/{userId}/roles")
    public ResponseEntity<ApiResponse<UpdateUserRoleResponseDto>> grantAdminRole(@PathVariable Long userId) {

        UpdateUserRoleResponseDto responseDto = authService.grantAdminRole(userId);

        return ApiResponse.onSuccess(SuccessStatus.ROLE_UPDATE_SUCCESS, responseDto);
    }
}
