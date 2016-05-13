package de.tum.communication.protocol;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Created by Nicolas Frinker on 12/05/16.
 */

/**
 *  Gossip notification message
 *  see Project specification 3.1.3
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class GossipNotificationMessage extends Message {
    private int datatype;
    private ByteSerializable payload;

    @Builder
    public GossipNotificationMessage(Integer datatype, ByteSerializable payload) {
        super((short) (2 * WORD_LENGTH + payload.getBytes().size()), MessageType.GOSSIP_NOTIFICATION);
        this.datatype = datatype;
        this.payload = payload;
    }

    @Override
    public List<Byte> getBytes() {
        byte[] headerBytes = getHeaderBytes();
        byte[] reservedBytes = new byte[2];
        byte[] datatypeBytes = Ints.toByteArray(datatype);
        List<Byte> bytelist = new ArrayList<Byte>(Bytes.asList(Bytes.concat(headerBytes, reservedBytes, datatypeBytes)));
        bytelist.addAll(payload.getBytes());
        return bytelist;
    }
}
