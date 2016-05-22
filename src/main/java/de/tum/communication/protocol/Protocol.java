package de.tum.communication.protocol;

import de.tum.communication.protocol.messages.Message;

import java.util.List;

/**
 * Created by Alexandru Obada on 12/05/16.
 */

/**
 * Given a sequence of bytes, identifies and builds a message
 */
public interface Protocol {
    Message deserialize(List<Byte> data);
}
