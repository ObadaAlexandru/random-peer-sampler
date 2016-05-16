package de.tum.communication.protocol;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;

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
    private int datatype;
    private ByteSerializable payload;

    @Builder
    public GossipAnnounceMessage(Short ttl, Integer datatype, ByteSerializable payload) {
        super((short) (2 * WORD_LENGTH + payload.getBytes().size()), MessageType.GOSSIP_ANNOUNCE);
        this.ttl = ttl;
        this.datatype = datatype;
        this.payload = payload;
    }

    @Override
    public List<Byte> getBytes() {
        byte[] headerBytes = getHeaderBytes();
        byte[] ttlBytes = Shorts.toByteArray(ttl);
        byte[] reservedBytes = new byte[1];
        byte[] datatypeBytes = Ints.toByteArray(datatype);
        List<Byte> bytelist = new ArrayList<Byte>(Bytes.asList(Bytes.concat(headerBytes, ttlBytes, reservedBytes, datatypeBytes)));
        bytelist.addAll(payload.getBytes());
        return bytelist;
    }
}
