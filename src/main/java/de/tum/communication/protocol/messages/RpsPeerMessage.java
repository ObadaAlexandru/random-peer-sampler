package de.tum.communication.protocol.messages;

import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.SerializablePeer;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * Created by Alexandru Obada on 11/05/16.
 */

/**
 * Random Peer Sampling message
 * see Project specification 3.3.2
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class RpsPeerMessage extends PeerMessage {
    @Builder
    public RpsPeerMessage(@NonNull SerializablePeer peer) {
        super(peer, MessageType.RPS_PEER);
    }
}