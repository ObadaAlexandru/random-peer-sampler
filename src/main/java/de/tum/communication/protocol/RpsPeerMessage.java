package de.tum.communication.protocol;

import java.util.List;

import com.google.common.primitives.Bytes;

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
public class RpsPeerMessage extends Message {
    private Peer peer;

    @Builder
    public RpsPeerMessage(@NonNull Peer peer) {
        super(computeMessageSize(peer), MessageType.RPS_PEER);
        this.peer = peer;
    }

    @Override
    public List<Byte> getBytes() {
        byte[] headerBytes = getHeaderBytes();
        byte[] messageBytes = Bytes.concat(headerBytes, peer.getBytes());
        return Bytes.asList(messageBytes);
    }

    private static short computeMessageSize(Peer peer) {
        return (short) (WORD_LENGTH + peer.getSize());
    }
}
