package de.tum.communication.exceptions;

import de.tum.common.exceptions.BaseException;
import lombok.NonNull;

import static de.tum.common.exceptions.ErrorCode.UNKNOWN_MESSAGE_TYPE;

/**
 * Created by Alexandru Obada on 12/05/16.
 */

/**
 * Exception that is thrown when we receive an unknown message type
 */
public class UnknownMessageTypeException extends BaseException {
    public UnknownMessageTypeException() {
        super(UNKNOWN_MESSAGE_TYPE);
    }

    public UnknownMessageTypeException(@NonNull String message) {
        super(UNKNOWN_MESSAGE_TYPE, message);
    }
}