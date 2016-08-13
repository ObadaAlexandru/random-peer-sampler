package de.tum.common.exceptions;

/**
 * Created by Nicolas Frinker on 07/06/16.
 */

/**
 * Failed deserializing peer message
 */
public class PeerDeserializationException extends ErrorCodeException {
    public PeerDeserializationException() {
        super(ErrorCode.INVALID_PEER_SERIALIZATION);
    }

    public PeerDeserializationException(String message) {
        super(ErrorCode.INVALID_PEER_SERIALIZATION, message);
    }
}
