package com.example.baro_15.common.security.config;

import com.example.baro_15.common.security.handler.CustomAccessDeniedHandler;
import com.example.baro_15.common.security.handler.CustomAuthenticationEntryPoint;
import com.example.baro_15.common.security.jwt.JwtAuthenticationFilter;
import com.example.baro_15.common.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.AntPathMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, String> redisTemplate;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화 (JWT는 stateless하므로 필요 없음)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 비활성화

                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 허용할 경로
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/docs"// swagger 문서 접근 허용(API 테스트 편의성 목적)
                        ).permitAll()


                        // 특정 역할만 접근 가능한 경로 설정 예시 (필요시 추가)
                        //.requestMatchers("/admin/**").hasRole("ADMIN")

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception // 예외 처리 설정
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 로그인x 일 경우 발동
                        .accessDeniedHandler(customAccessDeniedHandler) // 로그인o, 권한x 일 경우 발동
                )
                // JWT 인증 필터 등록
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtils, redisTemplate, new AntPathMatcher()),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
