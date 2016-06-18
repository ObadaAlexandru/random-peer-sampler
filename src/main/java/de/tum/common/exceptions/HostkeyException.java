package de.tum.common.exceptions;

/**
 * Created by Alexandru Obada on 31/05/16.
 */
public class HostkeyException extends ErrorCodeException {
    public HostkeyException() {
        super(ErrorCode.INVALID_HOSTKEY);
    }

    public HostkeyException(String message) {
        super(ErrorCode.INVALID_HOSTKEY, message);
    }
}