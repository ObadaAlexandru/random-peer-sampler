package de.tum.communication.protocol;

import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Shorts;
import de.tum.communication.protocol.messages.Message;
import de.tum.sampling.entity.Peer;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.net.Inet4Address;
import java.util.List;

@AllArgsConstructor
@Value
public class SerializablePeer implements ByteSerializable {
    public static final int IDENTIFIER_LENGTH = 32;

    private Peer peer;

    /**
     * Get byte array to be sent in a Message
     */
    public List<Byte> getBytes() {
        byte[] identifierBytes = BaseEncoding.base16().decode(peer.getIdentifier());
        byte[] portBytes = Shorts.toByteArray(peer.getPort().shortValue());
        byte[] addressBytes = peer.getAddress().getAddress();
        byte[] addressType = (peer.getAddress() instanceof Inet4Address) ? Shorts.toByteArray((short) 4) : Shorts.toByteArray((short) 6);
        byte[] messageBytes = Bytes.concat(identifierBytes, portBytes, addressType, addressBytes);
        return Bytes.asList(messageBytes);
    }

    /**
     * Get size of peer encoded for a Message
     */
    public short getSize() {
        return (short) (Message.WORD_LENGTH + peer.getAddress().getAddress().length + IDENTIFIER_LENGTH);
    }
}
