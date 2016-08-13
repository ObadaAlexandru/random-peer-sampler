package de.tum.communication.protocol;

import java.net.Inet4Address;
import java.util.List;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Shorts;

import de.tum.communication.protocol.messages.Message;
import de.tum.sampling.entity.Peer;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * A wrapper for peer objects to make them serializable.
 */
@AllArgsConstructor
@Value
public class SerializablePeer implements ByteSerializable {
    private Peer peer;

    /**
     * Get byte array to be sent in a Message
     */
    @Override
    public List<Byte> getBytes() {
        byte[] portBytes = Shorts.toByteArray(peer.getPort().shortValue());
        byte[] addressBytes = peer.getAddress().getAddress();
        byte[] addressType = (peer.getAddress() instanceof Inet4Address) ? Shorts.toByteArray((short) 4) : Shorts.toByteArray((short) 6);
        byte[] hostkeyBytes = peer.getHostkey().getEncoded();
        byte[] messageBytes = Bytes.concat(portBytes, addressType, addressBytes, hostkeyBytes);
        return Bytes.asList(messageBytes);
    }

    /**
     * Get size of peer encoded for a Message
     */
    public short getSize() {
        return (short) (Message.WORD_LENGTH + peer.getAddress().getAddress().length + peer.getHostkey().getEncoded().length);
    }
}
