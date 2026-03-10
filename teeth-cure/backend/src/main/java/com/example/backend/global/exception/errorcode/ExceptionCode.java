package com.example.backend.global.exception.errorcode;

import org.springframework.http.HttpStatus;

public interface ExceptionCode {

    HttpStatus getHttpStatus();

    String getExceptionCode();

    String getExceptionMessage();
}
