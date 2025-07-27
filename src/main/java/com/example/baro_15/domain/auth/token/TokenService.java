package com.example.baro_15.domain.auth.token;

import com.example.baro_15.common.exception.ApiException;
import com.example.baro_15.common.security.jwt.JwtUtils;
import com.example.baro_15.common.status.ErrorStatus;
import com.example.baro_15.domain.auth.dto.response.TokenResponseDto;
import com.example.baro_15.domain.user.domain.User;
import com.example.baro_15.domain.user.dto.UserResponseDto;
import com.example.baro_15.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenService {

    private final JwtUtils jwtUtils;

    public TokenResponseDto issueToken(UserResponseDto user) {

        // Access & Refresh Token 발급
        String accessToken = jwtUtils.generateAccessToken(user);

        // Dto 로 변환
        return new TokenResponseDto(accessToken);
    }
}
