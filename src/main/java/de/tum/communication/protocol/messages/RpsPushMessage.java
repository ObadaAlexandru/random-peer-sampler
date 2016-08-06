package de.tum.communication.protocol.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Bytes;

import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.SerializablePeer;
import de.tum.communication.protocol.Token;
import lombok.Builder;
import lombok.NonNull;

/**
 * Created by Alexandru Obada on 14/06/16.
 */

/**
 * Used to push the identity of the current node to other peers
 */
public class RpsPushMessage extends Message {
    private SerializablePeer peer;
    private Token token;

    @Builder
    public RpsPushMessage(@NonNull SerializablePeer peer, @NonNull Token token) {
        super((short) (WORD_LENGTH + Token.TOKEN_LENGTH + peer.getSize()), MessageType.RPS_PUSH);
        this.peer = peer;
        this.token = token;
    }

    @Override
    public List<Byte> getBytes() {
        byte[] headerBytes = getHeaderBytes();
        List<Byte> result = new ArrayList<>(Bytes.asList(headerBytes));
        result.addAll(this.token.getBytes());
        result.addAll(this.peer.getBytes());
        return result;
    }

    public Token getToken() {
        return this.token;
    }

    public SerializablePeer getPeer() {
        return this.peer;
    }
}