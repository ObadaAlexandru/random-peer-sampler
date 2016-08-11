package de.tum.common.exceptions;

import static de.tum.common.exceptions.ErrorCode.UNSUCCESSFUL_BOOTSTRAP;

/**
 *  Failed loading bootstrap file
 */
public class BootstrapException extends ErrorCodeException {
    public BootstrapException() {
        super(UNSUCCESSFUL_BOOTSTRAP);
    }

    public BootstrapException(String message) {
        super(UNSUCCESSFUL_BOOTSTRAP, message);
    }
}