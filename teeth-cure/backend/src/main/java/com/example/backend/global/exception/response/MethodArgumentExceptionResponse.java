package com.example.backend.global.exception.response;

import com.example.backend.global.exception.errorcode.ExceptionCode;
import java.util.Map;

public record MethodArgumentExceptionResponse(
        String exceptionCode,
        String exceptionMessage,
        Map<String, String> validateErrors
) {

    public static MethodArgumentExceptionResponse from(
            ExceptionCode code,
            Map<String, String> validateErrors
    ) {
        return new MethodArgumentExceptionResponse(
                code.getExceptionCode(),
                code.getExceptionMessage(),
                validateErrors
        );
    }

}
