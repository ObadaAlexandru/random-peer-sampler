package de.tum.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNKNOWN_MESSAGE_TYPE("ERR-001", "Message type unknown");

    private String code;
    private String defaultMessage;
}
