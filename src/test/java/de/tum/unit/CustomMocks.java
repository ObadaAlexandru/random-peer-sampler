package de.tum.unit;

import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.SerializablePeer;
import de.tum.sampling.entity.Peer;
import org.mockito.Mockito;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

class CustomMocks {
    static Message getMessage(MessageType messageType) {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getType()).thenReturn(messageType);
        Mockito.when(message.getBytes()).thenReturn(new ArrayList<>());
        return message;
    }
    
    static SerializablePeer getPeer(String address, short port, String identifier) throws UnknownHostException {
        Peer peer = Peer.builder()
                .address(InetAddress.getByName(address))
                .identifier(identifier.toUpperCase())
                .port((int) port).build();
        return new SerializablePeer(peer);
    }
}
