package de.tum.communication.protocol.messages;

import com.google.common.primitives.Bytes;
import de.tum.communication.protocol.MessageType;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

/**
 * Created by Alexandru Obada on 23/05/16.
 */

/**
 * RPS view exchange acknowledgement
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class RpsAckMessage extends Message {

    public RpsAckMessage() {
        super(WORD_LENGTH, MessageType.RPS_ACK);
    }

    @Override
    public List<Byte> getBytes() {
        return Bytes.asList(getHeaderBytes());
    }
}
