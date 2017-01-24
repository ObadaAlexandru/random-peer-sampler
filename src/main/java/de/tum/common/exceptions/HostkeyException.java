package de.tum.common.exceptions;

/**
 * Created by Alexandru Obada on 31/05/16.
 */

/**
 * Invalid or missing hostkey
 */
public class HostkeyException extends BaseException {
    public HostkeyException() {
        super(ErrorCode.INVALID_HOSTKEY);
    }

    public HostkeyException(String message) {
        super(ErrorCode.INVALID_HOSTKEY, message);
    }
}
