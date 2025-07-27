package com.example.baro_15.common.security.auth;

import com.example.baro_15.common.exception.ApiException;
import com.example.baro_15.common.status.ErrorStatus;
import com.example.baro_15.domain.user.domain.User;
import com.example.baro_15.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Service
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email {}", email);
                    throw new ApiException(ErrorStatus.USER_NOT_FOUND);
                });

        // 비밀번호가 null 이면 예외
        if (!StringUtils.hasText(user.getPassword())) {
            throw new ApiException(ErrorStatus.INVALID_PASSWORD);
        }

        return new CustomUserDetails(user);
    }
}
