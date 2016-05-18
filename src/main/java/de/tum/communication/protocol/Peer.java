package de.tum.communication.protocol;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;

import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Shorts;

import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class Peer implements ByteSerializable {
	public static final int IDENTIFIER_LENGTH = 32;
	
    String identifier;
    InetAddress address;
    short port;
    
    /**
     * Get byte array to be sent in a Message
     * 
     * @return
     */
    public List<Byte> getBytes() {
        byte[] identifierBytes = BaseEncoding.base16().decode(identifier);
        byte[] portBytes = Shorts.toByteArray(port);
        byte[] addressBytes = address.getAddress();
        byte[] addressType = (address instanceof Inet4Address) ?  Shorts.toByteArray((short) 4) : Shorts.toByteArray((short) 6);
        byte[] messageBytes = Bytes.concat(identifierBytes, portBytes, addressType, addressBytes);
        return Bytes.asList(messageBytes);
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
