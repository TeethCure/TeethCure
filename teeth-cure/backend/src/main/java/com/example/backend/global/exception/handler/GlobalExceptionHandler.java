package com.example.backend.global.exception.handler;

import com.example.backend.global.exception.GlobalException;
import com.example.backend.global.exception.errorcode.ExceptionCode;
import com.example.backend.global.exception.errorcode.InternalServerExceptionCode;
import com.example.backend.global.exception.response.ExceptionResponse;
import com.example.backend.global.exception.response.MethodArgumentExceptionResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = GlobalException.class)
    public ResponseEntity<ExceptionResponse> handleGlobalException(GlobalException e) {
        ExceptionCode code = e.getExceptionCode();
        log.info("Exception Occurred !! HttpStatus: {}, Code: {}, Message: {}",
                code.getHttpStatus(),
                code.getExceptionCode(),
                code.getExceptionMessage()
        );
        return ResponseEntity.status(code.getHttpStatus())
                .body(ExceptionResponse.from(code));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<MethodArgumentExceptionResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.info("MethodArgumentNotValidException Occurred: {}", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(MethodArgumentExceptionResponse.from(
                        InternalServerExceptionCode.INVALID_INPUT_VALUE, errors)
                );
    }
}
