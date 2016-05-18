package de.tum.communication.exceptions;

import de.tum.common.exceptions.ErrorCodeException;
import lombok.NonNull;

import static de.tum.common.exceptions.ErrorCode.UNKNOWN_MESSAGE_TYPE;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
public class UnknownMessageTypeException extends ErrorCodeException {
    public UnknownMessageTypeException() {
        super(UNKNOWN_MESSAGE_TYPE);
    }

    public UnknownMessageTypeException(@NonNull String message) {
        super(UNKNOWN_MESSAGE_TYPE, message);
    }
}