package de.tum.common.exceptions;

/**
 * Created by Nicolas Frinker on 07/06/16.
 */
public class PeerDeserialisationException extends ErrorCodeException {
    public PeerDeserialisationException() {
        super(ErrorCode.INVALID_PEER_SERIALIZATION);
    }

    public PeerDeserialisationException(String message) {
        super(ErrorCode.INVALID_PEER_SERIALIZATION, message);
    }
}
