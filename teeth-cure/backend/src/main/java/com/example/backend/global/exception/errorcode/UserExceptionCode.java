package com.example.backend.global.exception.errorcode;

import static com.example.backend.account.domain.Password.HASHED_ALGORITHM;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserExceptionCode implements ExceptionCode {
    INVALID_PASSWORD_ALGORITHM(HttpStatus.INTERNAL_SERVER_ERROR, "U001",
            HASHED_ALGORITHM + "암호화 중 오류 발생"),
    ;

    private final HttpStatus httpStatus;
    private final String exceptionMessage;
    private final String exceptionCode;
}
