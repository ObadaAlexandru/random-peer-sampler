package de.tum.communication.protocol.messages;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Shorts;
import de.tum.communication.protocol.ByteSerializable;
import de.tum.communication.protocol.MessageType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicolas Frinker on 12/05/16.
 */

/**
 *  Gossip announce message
 *  see Project specification 3.1.1
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class GossipAnnounceMessage extends Message {
    private short ttl;
    private short dataType;
    private ByteSerializable payload;

    @Builder
    public GossipAnnounceMessage(Short ttl, Short datatype, ByteSerializable payload) {
        super((short) (2 * WORD_LENGTH + payload.getBytes().size()), MessageType.GOSSIP_ANNOUNCE);
        this.ttl = ttl;
        this.dataType = datatype;
        this.payload = payload;
    }

    @Override
    public List<Byte> getBytes() {
        byte[] headerBytes = getHeaderBytes();
        byte[] ttlBytes = Shorts.toByteArray(ttl);
        byte[] reservedBytes = new byte[] {0};
        byte[] dataTypeBytes = Shorts.toByteArray(dataType);
        List<Byte> byteList = new ArrayList<>(Bytes.asList(Bytes.concat(headerBytes, ttlBytes, reservedBytes, dataTypeBytes)));
        byteList.addAll(payload.getBytes());
        return byteList;
    }
}
