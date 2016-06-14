package de.tum.communication.protocol.messages;

import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.SerializablePeer;
import lombok.Builder;
import lombok.NonNull;

/**
 * Created by Alexandru Obada on 14/06/16.
 */

/**
 * Used to push the identity of the current node to other peers
 */
public class RpsPushMessage extends PeerMessage {
    @Builder
    public RpsPushMessage(@NonNull SerializablePeer peer) {
        super(peer, MessageType.RPS_PUSH);
    }
}