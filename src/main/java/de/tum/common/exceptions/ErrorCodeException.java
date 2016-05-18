package de.tum.common.exceptions;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
public class ErrorCodeException extends RuntimeException {
    private ErrorCode errorCode;
    private String message;

    public ErrorCodeException(ErrorCode code) {
        errorCode = code;
        message = code.getDefaultMessage();
    }

    public ErrorCodeException(ErrorCode code, String message) {
        errorCode = code;
        this.message = message;
    }

    public String toString() {
        return String.format("(%s) - %s", errorCode.getCode(), message);
    }
}
