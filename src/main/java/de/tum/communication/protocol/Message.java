package de.tum.communication.protocol;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Shorts;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Alexandru Obada on 11/05/16.
 */

/**
 * Base inter module communication message
 * see Project Specification Figure 1: Message header format
 */

@AllArgsConstructor
@Getter
abstract public class Message implements ByteSerializable {
    public static final short WORD_LENGTH = 4;
    /**
     * Message size in Bytes including the header
     */
    protected final short size;
    protected final MessageType type;

    protected byte[] getHeaderBytes() {
        byte[] sizeBytes = Shorts.toByteArray(size);
        byte[] typeBytes = Shorts.toByteArray(type.getValue());
        return Bytes.concat(sizeBytes, typeBytes);
    }
}
