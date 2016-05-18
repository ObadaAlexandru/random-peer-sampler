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

    public static MessageType getType(short type) {
        switch (type) {
            case 500:
                return GOSSIP_ANNOUNCE;
            case 501:
                return GOSSIP_NOTIFY;
            case 502:
                return GOSSIP_NOTIFICATION;
            case 520:
                return NSE_QUERY;
            case 521:
                return NSE_ESTIMATE;
            case 540:
                return RPS_QUERY;
            case 541:
                return RPS_PEER;
            default:
                throw new IllegalArgumentException("");
        }
    }
}
