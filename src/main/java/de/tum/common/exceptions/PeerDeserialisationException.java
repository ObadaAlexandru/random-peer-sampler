package de.tum.common.exceptions;

/**
 * Created by Nicolas Frinker on 07/06/16.
 */
public class PeerDeserialisationException extends ErrorCodeException {
    public PeerDeserialisationException() {
        super(ErrorCode.INVALID_PEERSERIALISATION);
    }

    public PeerDeserialisationException(String message) {
        super(ErrorCode.INVALID_PEERSERIALISATION, message);
    }
}
