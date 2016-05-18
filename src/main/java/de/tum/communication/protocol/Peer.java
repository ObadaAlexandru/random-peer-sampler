package de.tum.communication.protocol;

import java.net.Inet4Address;
import java.net.InetAddress;

import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Shorts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Peer {
	public static final int IDENTIFIER_LENGTH = 32;
	
    private final String identifier;
    private final InetAddress address;
    private final short port;
    
    /**
     * Get byte array to be sent in a Message
     * 
     * @return
     */
    public byte[] getBytes() {
        byte[] identifierBytes = BaseEncoding.base16().decode(identifier);
        byte[] portBytes = Shorts.toByteArray(port);
        byte[] addressBytes = address.getAddress();
        byte[] addressType = (address instanceof Inet4Address) ? new byte[]{0, 0} : new byte[]{(byte) 0xff, (byte) 0xff};
        byte[] messageBytes = Bytes.concat(identifierBytes, portBytes, addressType, addressBytes);
        return messageBytes;
    }
    
    /**
     * Get size of peer encoded for a Message
     * 
     * @return
     */
    public short getSize() {
    	return (short) (Message.WORD_LENGTH + address.getAddress().length + IDENTIFIER_LENGTH);
    }
}
