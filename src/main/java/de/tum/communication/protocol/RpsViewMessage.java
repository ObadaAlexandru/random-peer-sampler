package de.tum.communication.protocol;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Bytes;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * Created by Nicolas Frinker on 18/05/16.
 */

/**
 * Random Peer Sampling view message
 * Sent between our RPS modules for exchanging views
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class RpsViewMessage extends Message {
    private final Peer source;
    private final List<Peer> peers;

    @Builder
    public RpsViewMessage(@NonNull Peer source, @NonNull List<Peer> peers) {
        super(computeMessageSize(source, peers), MessageType.RPS_VIEW);
        this.source = source;
        this.peers = peers;
    }

    @Override
    public List<Byte> getBytes() {
        List<Byte> result = new ArrayList<Byte>(Bytes.asList(getHeaderBytes())); 
        result.addAll(this.source.getBytes());
        for (Peer peer : this.peers) {
            result.addAll(peer.getBytes());
        }
        return result;
    }

    private static short computeMessageSize(Peer source, List<Peer> peers) {
        return (short) (WORD_LENGTH + source.getSize()
                + peers.stream().map(Peer::getSize).mapToInt(Number::intValue).sum());
    }
}
