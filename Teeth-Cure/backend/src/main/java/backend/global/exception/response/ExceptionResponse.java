package backend.global.exception.response;


import backend.global.exception.errorcode.ExceptionCode;

public record ExceptionResponse(
        String exceptionCode,
        String exceptionMessage
) {

    public static ExceptionResponse from(ExceptionCode code) {
        return new ExceptionResponse(code.getExceptionCode(), code.getExceptionMessage());
    }
}
