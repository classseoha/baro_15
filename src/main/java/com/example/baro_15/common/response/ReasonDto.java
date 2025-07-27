package com.example.baro_15.common.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ReasonDto {

    private final boolean isSuccess;

    private HttpStatus httpStatus;

    private final String code;

    private final String message;

}
