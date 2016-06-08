package de.tum.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNKNOWN_MESSAGE_TYPE("ERR-001", "Message type unknown"),
    INVALID_CONFIGURATION("ERR-002", "Invalid configuration"),
    INVALID_HOSTKEY("ERR-003", "Invalid host key"),
    INVALID_PEERSERIALISATION("ERR-004", "Invalid serialized peer");

    private String code;
    private String defaultMessage;
}
