package de.tum.communication.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Alexandru Obada on 10/05/16.
 */

/**
 *  Defines the message types as described in the Project Specification A. Message Types
 */

@Getter
@AllArgsConstructor
public enum MessageType {
    NSE_QUERY((short) 520),
    NSE_ESTIMATE((short) 521),
    RPS_QUERY((short) 540),
    RPS_PEER((short) 541);

    private short value;
}
