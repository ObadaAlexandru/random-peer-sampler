package de.tum.communication.protocol.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Shorts;

import de.tum.communication.protocol.MessageType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Created by Nicolas Frinker on 12/05/16.
 */

/**
 * Gossip Notify Message
 * see Project Specification 3.1.2
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class GossipNotifyMessage extends Message {
    private short dataType;

    @Builder
    public GossipNotifyMessage(Short datatype) {
        super((short) (2 * WORD_LENGTH), MessageType.GOSSIP_NOTIFY);
        this.dataType = datatype;
    }

    @Override
    public List<Byte> getBytes() {
        byte[] headerBytes = getHeaderBytes();
        byte[] reservedBytes = new byte[] {0, 0};
        byte[] dataTypeBytes = Shorts.toByteArray(dataType);
        List<Byte> byteList = new ArrayList<>(Bytes.asList(Bytes.concat(headerBytes, reservedBytes, dataTypeBytes)));
        return byteList;
    }
}