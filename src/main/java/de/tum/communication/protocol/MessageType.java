package de.tum.communication.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Alexandru Obada on 10/05/16.
 */

/**
 * Defines the message types as described in the Project Specification A. Message Types
 */

@Getter
@AllArgsConstructor
public enum MessageType {
    GOSSIP_ANNOUNCE((short) 500),
    GOSSIP_NOTIFY((short) 501),
    GOSSIP_NOTIFICATION((short) 502),
    GOSSIP_VALIDATION((short) 503),
    NSE_QUERY((short) 520),
    NSE_ESTIMATE((short) 521),
    RPS_QUERY((short) 540),
    RPS_PEER((short) 541),
    RPS_VIEW((short) 542),
    RPS_PUSH((short) 543),
    RPS_PING((short) 544);

    private short value;

    public static MessageType getType(short type) {
        switch (type) {
            case 500:
                return GOSSIP_ANNOUNCE;
            case 501:
                return GOSSIP_NOTIFY;
            case 502:
                return GOSSIP_NOTIFICATION;
            case 503:
                return GOSSIP_VALIDATION;
            case 520:
                return NSE_QUERY;
            case 521:
                return NSE_ESTIMATE;
            case 540:
                return RPS_QUERY;
            case 541:
                return RPS_PEER;
            case 542:
                return RPS_VIEW;
            case 543:
                return RPS_PUSH;
            case 544:
                return RPS_PING;
            default:
                throw new IllegalArgumentException("Unknown message type " + type);
        }
    }
}
