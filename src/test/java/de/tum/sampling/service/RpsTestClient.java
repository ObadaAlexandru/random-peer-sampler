package de.tum.sampling.service;

import static de.tum.communication.service.Module.Service.BASE;

import java.net.SocketAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tum.communication.protocol.messages.Message;
import de.tum.communication.service.Client;
import de.tum.communication.service.Module;
import de.tum.communication.service.Receiver;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Service
public class RpsTestClient implements Client {
    private Client client;

    @Autowired
    public RpsTestClient(@Module(BASE) Client client) {
        this.client = client;
    }

    @Override
    public Void send(Message data) {
        throw new NotImplementedException();
    }

    @Override
    public Void send(Message data, SocketAddress address) {
        return client.send(data, address);
    }

    @Override
    public void setReceiver(Receiver<Message> receiver) {
        this.client.setReceiver(receiver);
    }

    @Override
    public void sendPersistent(Message data, SocketAddress address) {
        throw new NotImplementedException();
    }
}
