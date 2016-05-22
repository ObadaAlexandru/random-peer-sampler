package de.tum.communication.protocol.messages;

import com.google.common.primitives.Bytes;
import de.tum.communication.protocol.MessageType;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

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

    public GossipNotifyMessage() {
        super(WORD_LENGTH, MessageType.GOSSIP_NOTIFY);
    }

    @Override
    public List<Byte> getBytes() {
        return Bytes.asList(getHeaderBytes());
    }
}