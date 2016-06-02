package de.tum.communication.protocol.messages;

import com.google.common.primitives.Bytes;
import de.tum.communication.protocol.MessageType;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

/**
 * Created by Alexandru Obada on 11/05/16.
 */

/**
 *  Random Peer Sampling query message
 *  see Project specification 3.3.1
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class RpsQueryMessage extends Message {

    public RpsQueryMessage() {
        super(WORD_LENGTH, MessageType.RPS_QUERY);
    }

    @Override
    public List<Byte> getBytes() {
        return Bytes.asList(getHeaderBytes());
    }
}