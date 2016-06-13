package de.tum.communication.protocol.messages;

import com.google.common.primitives.Bytes;
import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.SerializablePeer;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandru Obada on 11/05/16.
 */

/**
 * Random Peer Sampling message
 * see Project specification 3.3.2
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class RpsPeerMessage extends Message {
    private SerializablePeer peer;

    @Builder
    public RpsPeerMessage(@NonNull SerializablePeer peer) {
        super(computeMessageSize(peer), MessageType.RPS_PEER);
        this.peer = peer;
    }

    @Override
    public List<Byte> getBytes() {
        List<Byte> result = new ArrayList<>(Bytes.asList(getHeaderBytes()));
        result.addAll(peer.getBytes());
        return result;
    }

    private static short computeMessageSize(SerializablePeer peer) {
        return (short) (WORD_LENGTH + peer.getSize());
    }
}