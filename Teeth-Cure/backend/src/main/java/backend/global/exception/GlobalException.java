package backend.global.exception;

import backend.global.exception.errorcode.ExceptionCode;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public GlobalException(ExceptionCode code) {
        super("Exception: &s, Code: %s, Message: %s".formatted(
                code.getHttpStatus(),
                code.getExceptionCode(),
                code.getExceptionMessage()
        ));
        this.exceptionCode = code;
    }
}
