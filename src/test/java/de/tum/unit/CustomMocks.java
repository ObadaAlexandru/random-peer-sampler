package de.tum.unit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.mockito.Mockito;

import de.tum.communication.protocol.Message;
import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.Peer;

class CustomMocks {
    static Message getMessage(MessageType messageType) {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getType()).thenReturn(messageType);
        Mockito.when(message.getBytes()).thenReturn(new ArrayList<>());
        return message;
    }
    
    static Peer getPeer(String address, short port, String identifier) throws UnknownHostException {
        return new Peer(identifier.toUpperCase(), InetAddress.getByName(address), port);
    }
}
