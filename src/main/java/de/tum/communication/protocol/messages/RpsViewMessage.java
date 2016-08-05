package de.tum.communication.protocol.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Bytes;

import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.SerializablePeer;
import de.tum.communication.protocol.Token;
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
    private final SerializablePeer source;
    private final List<SerializablePeer> peers;
    private final Token token;

    @Builder
    public RpsViewMessage(@NonNull SerializablePeer source, @NonNull List<SerializablePeer> peers, @NonNull Token token) {
        super(computeMessageSize(source, peers), MessageType.RPS_VIEW);
        this.source = source;
        this.peers = peers;
        this.token = token;
    }

    @Override
    public List<Byte> getBytes() {
        List<Byte> result = new ArrayList<>(Bytes.asList(getHeaderBytes()));
        result.addAll(this.token.getBytes());
        result.addAll(this.source.getBytes());
        for (SerializablePeer peer : this.peers) {
            result.addAll(peer.getBytes());
        }
        return result;
    }

    public Token getToken() {
        return this.token;
    }

    private static short computeMessageSize(SerializablePeer source, List<SerializablePeer> peers) {
        return (short) (WORD_LENGTH + Token.TOKEN_LENGTH + source.getSize()
                + peers.stream().map(SerializablePeer::getSize).mapToInt(Number::intValue).sum());
    }
}
