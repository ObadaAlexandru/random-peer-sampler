package de.tum.communication.messages;

import java.util.List;

/**
 * Created by Alexandru Obada on 10/05/16.
 */
public interface ByteSerializable {

    /**
     * @return byte representation of the object
     */
    List<Byte> getBytes();
}
