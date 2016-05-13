package de.tum.communication.protocol;

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
    GOSSIP_ANNOUNCE((short) 500),
    GOSSIP_NOTIFY((short) 501),
    GOSSIP_NOTIFICATION((short) 502),
    NSE_QUERY((short) 520),
    NSE_ESTIMATE((short) 521),
    RPS_QUERY((short) 540),
    RPS_PEER((short) 541);

    private short value;
}
