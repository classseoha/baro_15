package com.example.baro_15.domain.user.dto;

import com.example.baro_15.domain.user.domain.User;
import com.example.baro_15.domain.user.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDto {

    private Long id;

    private String email;

    private UserRole userRole;

    public static UserResponseDto from(User user) {

        return new UserResponseDto(user.getId(), user.getEmail(), user.getUserRole());
    }

}
