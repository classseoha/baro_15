package com.example.baro_15.domain.auth.service;

import com.example.baro_15.common.exception.ApiException;
import com.example.baro_15.common.security.auth.CustomUserDetails;
import com.example.baro_15.common.security.jwt.JwtUtils;
import com.example.baro_15.common.status.ErrorStatus;
import com.example.baro_15.domain.auth.dto.request.LoginRequestDto;
import com.example.baro_15.domain.auth.dto.request.SignUpRequestDto;
import com.example.baro_15.domain.auth.dto.response.TokenResponseDto;
import com.example.baro_15.domain.auth.dto.response.UpdateUserRoleResponseDto;
import com.example.baro_15.domain.auth.token.RedisService;
import com.example.baro_15.domain.auth.token.TokenService;
import com.example.baro_15.domain.user.domain.User;
import com.example.baro_15.domain.user.domain.UserRole;
import com.example.baro_15.domain.user.dto.UserResponseDto;
import com.example.baro_15.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.Transient;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtils jwtUtils;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public TokenResponseDto signup(SignUpRequestDto requestDto) {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new ApiException(ErrorStatus.DUPLICATED_EMAIL);
        }

        User.UserBuilder builder = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()));

        User user = builder.build();
        userRepository.save(user);

        return tokenService.issueToken(UserResponseDto.from(user));
    }

    public TokenResponseDto login(LoginRequestDto loginRequestDto) {

        // 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );

        // 인증된 사용자 정보
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserResponseDto userDto = UserResponseDto.from(userDetails.getUser());

        // 토큰 발급
        return tokenService.issueToken(userDto);
    }

    public void logout(String token) {

        // token 유효성 검사
        if (!jwtUtils.validateToken(token)) {
            throw new ApiException(ErrorStatus.INVALID_ACCESS_TOKEN);
        }

        // AccessToken 블랙리스트 등록
        long expiration = jwtUtils.getRemainingExpiration(token);

        redisService.addToBlackList(token, expiration);
    }

    @Transient
    public UpdateUserRoleResponseDto grantAdminRole(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorStatus.USER_NOT_FOUND));

        user.updateRole(UserRole.ADMIN);

        userRepository.save(user);

        return UpdateUserRoleResponseDto.from(user);
    }
}
