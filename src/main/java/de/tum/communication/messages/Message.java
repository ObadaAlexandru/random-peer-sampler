package de.tum.communication.messages;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Shorts;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Created by Alexandru Obada on 11/05/16.
 */

/**
 *  Base inter module communication message
 *  see Project Specification Figure 1: Message header format
 */

@AllArgsConstructor
@Getter
abstract public class Message implements ByteSerializable {
    public static final short HEADER_LENGTH = 4;
    /**
     *  Message size in Bytes including the header
     */
    protected final short size;
    protected final MessageType type;

    public List<Byte> getHeaderBytes() {
        byte[] sizeBytes = Shorts.toByteArray(size);
        byte[] typeBytes = Shorts.toByteArray(type.getValue());
        return Bytes.asList(Bytes.concat(sizeBytes, typeBytes));
    }
}
