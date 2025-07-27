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
import com.example.baro_15.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 회원가입_성공() {

        // given
        SignUpRequestDto request = new SignUpRequestDto("user@example.com", "test1234!");
        User mockUser = User.builder()
                .email(request.getEmail())
                .password("encoded")
                .userRole(UserRole.USER)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(tokenService.issueToken(any())).thenReturn(new TokenResponseDto("access"));

        // when
        TokenResponseDto response = authService.signup(request);

        // then
        assertThat(response.accessToken()).isEqualTo("access");
    }

    @Test
    void 회원가입_중복이메일() {

        // given
        SignUpRequestDto request = new SignUpRequestDto("duplicate@example.com", "1234");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // when
        Throwable thrown = catchThrowable(() -> authService.signup(request));

        // then
        assertThat(thrown).isInstanceOf(ApiException.class);
        assertThat(((ApiException) thrown).getErrorCode()).isEqualTo(ErrorStatus.DUPLICATED_EMAIL);
    }

    @Test
    void 로그인_성공() {

        // given
        LoginRequestDto request = new LoginRequestDto("login@example.com", "test1234");

        User user = User.builder()
                .email(request.getEmail())
                .password("encoded")
                .userRole(UserRole.USER)
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenService.issueToken(any())).thenReturn(new TokenResponseDto("access-token"));

        // when
        TokenResponseDto tokenResponse = authService.login(request);

        // then
        assertThat(tokenResponse.accessToken()).isEqualTo("access-token");
    }

    @Test
    void 관리자권한_부여_성공() {

        // given
        Long userId = 1L;
        User user = User.builder()
                .email("user@example.com")
                .password("pass")
                .userRole(UserRole.USER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        UpdateUserRoleResponseDto result = authService.grantAdminRole(userId);

        // then
        assertThat(result.getUserRole()).isEqualTo(UserRole.ADMIN);
        verify(userRepository).save(user); // 수정된 권한 저장 확인
    }

    @Test
    void 관리자권한_부여_실패_존재하지않는사용자() {

        // given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> authService.grantAdminRole(999L));

        // then
        assertThat(thrown).isInstanceOf(ApiException.class);
        assertThat(((ApiException) thrown).getErrorCode()).isEqualTo(ErrorStatus.USER_NOT_FOUND);
    }







}