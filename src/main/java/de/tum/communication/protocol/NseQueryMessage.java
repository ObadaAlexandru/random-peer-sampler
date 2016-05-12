package de.tum.communication.protocol;

import com.google.common.primitives.Bytes;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

/**
 * Created by Alexandru Obada on 11/05/16.
 */

/**
 * Network Size Estimation Query Message
 * see Project Specification 3.2.1
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class NseQueryMessage extends Message {

    public NseQueryMessage() {
        super(WORD_LENGTH, MessageType.NSE_QUERY);
    }

    @Override
    public List<Byte> getBytes() {
        return Bytes.asList(getHeaderBytes());
    }
}