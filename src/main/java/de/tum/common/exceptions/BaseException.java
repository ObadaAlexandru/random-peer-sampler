package de.tum.common.exceptions;

/**
 * Created by Alexandru Obada on 12/05/16.
 */

/**
 * Base exception class adding an error code to each exception.
 */
public class BaseException extends RuntimeException {
    private ErrorCode errorCode;
    private String message;

    public BaseException(ErrorCode code) {
        errorCode = code;
        message = code.getDefaultMessage();
    }

    public BaseException(ErrorCode code, String message) {
        errorCode = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("(%s) - %s", errorCode.getCode(), message);
    }
}
