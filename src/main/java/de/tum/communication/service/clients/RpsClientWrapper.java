package de.tum.communication.service.clients;

import de.tum.communication.protocol.messages.Message;
import de.tum.communication.service.Client;
import de.tum.communication.service.Module;
import de.tum.communication.service.Receiver;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.SocketAddress;

import static de.tum.communication.service.Module.Service.BASE;

@Module(Module.Service.RPS)
public class RpsClientWrapper implements Client {
    private Client client;

    public RpsClientWrapper(@Module(BASE) Client client) {
        this.client = client;
    }

    @Override
    public void sendPersistent(Message data, SocketAddress address) {
        throw new NotImplementedException();
    }

    @Override
    public void setReceiver(Receiver<Message> receiver) {
        this.client.setReceiver(receiver);
    }

    @Override
    public Void send(Message data) {
        throw new NotImplementedException();
    }

    @Override
    public Void send(Message data, SocketAddress address) {
        return client.send(data, address);
    }
}
