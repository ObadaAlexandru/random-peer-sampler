package de.tum.communication.protocol.messages;

import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.SerializablePeer;
import lombok.Builder;
import lombok.NonNull;

/**
 * Created by Nicolas Frinker on 20/06/16.
 */

/**
 * Used to ping peers and check, if they are still up and running
 */
public class RpsPingMessage extends PeerMessage {
    @Builder
    public RpsPingMessage(@NonNull SerializablePeer peer) {
        super(peer, MessageType.RPS_PING);
    }
}