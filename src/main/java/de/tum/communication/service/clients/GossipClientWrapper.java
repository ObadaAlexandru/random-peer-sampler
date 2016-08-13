package de.tum.communication.service.clients;

import de.tum.communication.protocol.messages.Message;
import de.tum.communication.service.Client;
import de.tum.communication.service.Module;
import de.tum.communication.service.Receiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static de.tum.communication.service.Module.Service.BASE;

/**
 * A client to connect to the gossip module
 */
@Component
@Module(Module.Service.GOSSIP)
public class GossipClientWrapper implements Client {

    private Client client;
    private SocketAddress gossipAddress;

    @Autowired
    public GossipClientWrapper(@Module(BASE) Client client,
                               @Value("#{iniConfig.getGossipHost()}") InetAddress address,
                               @Value("#{iniConfig.getGossipPort()}") Integer port) {
        this.client = client;
        gossipAddress = new InetSocketAddress(address, port);
    }

    @Override
    public void sendPersistent(Message data, SocketAddress address) {
        client.sendPersistent(data, address);
    }

    @Override
    public void setReceiver(Receiver<Message> receiver) {
        client.setReceiver(receiver);
    }

    @Override
    public Void send(Message data) {
        client.sendPersistent(data, gossipAddress);
        return null;
    }

    @Override
    public Void send(Message data, SocketAddress address) {
        throw new NotImplementedException();
    }
}
