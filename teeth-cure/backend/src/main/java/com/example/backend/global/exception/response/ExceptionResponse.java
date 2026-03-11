package com.example.backend.global.exception.response;

import com.example.backend.global.exception.errorcode.ExceptionCode;

public record ExceptionResponse(
        String exceptionCode,
        String exceptionMessage
) {

    public static ExceptionResponse from(ExceptionCode code) {
        return new ExceptionResponse(code.getExceptionCode(), code.getExceptionMessage());
    }
}
