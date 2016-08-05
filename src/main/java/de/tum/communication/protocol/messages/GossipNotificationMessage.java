package de.tum.communication.protocol.messages;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Shorts;

import de.tum.communication.protocol.ByteSerializable;
import de.tum.communication.protocol.MessageType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
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
    private Short messageId;
    private Short dataType;
    private ByteSerializable payload;

    @Builder
    public GossipNotificationMessage(@NonNull Short messageId, @NonNull Short datatype, @NonNull ByteSerializable payload) {
        super((short) (2 * WORD_LENGTH + payload.getBytes().size()), MessageType.GOSSIP_NOTIFICATION);
        this.messageId = messageId;
        this.dataType = datatype;
        this.payload = payload;
    }

    @Override
    public List<Byte> getBytes() {
        byte[] headerBytes = getHeaderBytes();
        byte[] dataTypeBytes = Shorts.toByteArray(dataType);
        byte[] messageIdBytes = Shorts.toByteArray(messageId);
        List<Byte> byteList = new ArrayList<>(Bytes.asList(Bytes.concat(headerBytes, messageIdBytes, dataTypeBytes)));
        byteList.addAll(payload.getBytes());
        return byteList;
    }
}
