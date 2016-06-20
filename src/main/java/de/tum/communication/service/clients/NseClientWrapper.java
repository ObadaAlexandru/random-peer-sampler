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

@Component
@Module(Module.Service.NSE)
public class NseClientWrapper implements Client {
    private Client client;
    private SocketAddress nseAddress;

    @Autowired
    public NseClientWrapper(@Module(BASE) Client client,
                            @Value("#{iniConfig.getNseHost()}") InetAddress address,
                            @Value("#{iniConfig.getNsePort()}") Integer port) {
        this.client = client;
        nseAddress = new InetSocketAddress(address, port);
    }

    @Override
    public void sendPersistent(Message data, SocketAddress address) {
        throw new NotImplementedException();
    }

    @Override
    public void setReceiver(Receiver<Message> receiver) {
        client.setReceiver(receiver);
    }

    @Override
    public Void send(Message data) {
        return client.send(data, nseAddress);
    }

    @Override
    public Void send(Message data, SocketAddress address) {
        throw new NotImplementedException();
    }
}
