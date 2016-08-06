package de.tum.communication.protocol.messages;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Shorts;
import de.tum.communication.protocol.MessageType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandru Obada on 06/08/16.
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class GossipValidationMessage extends Message {
    private Short messageId;
    private byte valid;

    @Builder
    public GossipValidationMessage(@NonNull Short messageId, boolean valid) {
        super((short) (2 * WORD_LENGTH), MessageType.GOSSIP_VALIDATION);
        this.messageId = messageId;
        this.valid = valid ? (byte) 1 : (byte) 0;
    }

    @Override
    public List<Byte> getBytes() {
        byte[] headerBytes = getHeaderBytes();
        byte[] messageIdBytes = Shorts.toByteArray(messageId);
        byte[] validityBytes = {0, valid};
        return new ArrayList<>(Bytes.asList(Bytes.concat(headerBytes, messageIdBytes, validityBytes)));
    }
}
