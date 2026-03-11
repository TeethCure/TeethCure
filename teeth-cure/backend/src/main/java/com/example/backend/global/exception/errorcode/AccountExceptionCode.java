package com.example.backend.global.exception.errorcode;

import static com.example.backend.account.domain.Password.HASHED_ALGORITHM;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AccountExceptionCode implements ExceptionCode {
    INVALID_PASSWORD_ALGORITHM(HttpStatus.INTERNAL_SERVER_ERROR, "A001",
            HASHED_ALGORITHM + "암호화 중 오류 발생"),
    DUPLICATED_USER_ID(HttpStatus.CONFLICT, "A002", "이미 등록된 아이디입니다."),
    INVALID_USERNAME_PASSWORD(HttpStatus.UNAUTHORIZED, "A003", "잘못된 아이디 혹은 비밀번호 입니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "A004", "계정이 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String exceptionMessage;
    private final String exceptionCode;
}
