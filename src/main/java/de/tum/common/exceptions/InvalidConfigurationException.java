package de.tum.common.exceptions;

/**
 * Created by Alexandru Obada on 31/05/16.
 */
public class InvalidConfigurationException extends ErrorCodeException {
    public InvalidConfigurationException(String message) {
        super(ErrorCode.INVALID_CONFIGURATION, message);
    }
}
