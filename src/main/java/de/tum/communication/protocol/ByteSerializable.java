package de.tum.communication.protocol;

import java.util.List;

/**
 * Created by Alexandru Obada on 10/05/16.
 */

/**
 * Allows to get Byte representation of object
 */
public interface ByteSerializable {

    /**
     * @return byte representation of the object
     */
    List<Byte> getBytes();
}
