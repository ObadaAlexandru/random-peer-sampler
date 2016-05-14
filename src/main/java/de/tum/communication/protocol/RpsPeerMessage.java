package de.tum.communication.protocol;

import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Shorts;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.net.InetAddress;
import java.util.List;

/**
 * Created by Alexandru Obada on 11/05/16.
 */

/**
 * Random Peer Sampling message
 * see Project specification 3.3.2
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class RpsPeerMessage extends Message {
    public static final int IDENTIFIER_LENGTH = 32;
    private String identifier;
    private InetAddress address;
    private short port;

    @Builder
    public RpsPeerMessage(@NonNull String identifier, @NonNull InetAddress address, @NonNull Short port) {
        super(computeMessageSize(address), MessageType.RPS_PEER);
        this.identifier = identifier.toUpperCase();
        this.address = address;
        this.port = port;
    }

    @Override
    public List<Byte> getBytes() {
        byte[] headerBytes = getHeaderBytes();
        byte[] identifierBytes = BaseEncoding.base16().decode(identifier);
        byte[] portBytes = Shorts.toByteArray(port);
        byte[] addressBytes = address.getAddress();
        byte[] messageBytes = Bytes.concat(headerBytes, identifierBytes, portBytes, new byte[]{0, 0}, addressBytes);
        return Bytes.asList(messageBytes);
    }

    private static short computeMessageSize(InetAddress address) {
        return (short) (2 * WORD_LENGTH + address.getAddress().length + IDENTIFIER_LENGTH);
    }
}
