package com.example.baro_15.common.exception;

import com.example.baro_15.common.status.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException {

    private final BaseErrorCode errorCode;

}
