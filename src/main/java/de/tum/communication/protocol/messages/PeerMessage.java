package de.tum.communication.protocol.messages;

import com.google.common.primitives.Bytes;
import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.SerializablePeer;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandru Obada on 14/06/16.
 */
abstract class PeerMessage extends Message {
    private SerializablePeer peer;

    PeerMessage(@NonNull SerializablePeer peer, MessageType type) {
        super(computeMessageSize(peer), type);
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

    public SerializablePeer getPeer() {
        return this.peer;
    }
}
