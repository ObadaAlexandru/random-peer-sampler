package de.tum.communication.service;

import de.tum.communication.protocol.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Service
@Module(Module.Service.GOSSIP)
public class GossipClient implements Client {
    @Override
    public void setReceiver(Receiver<Message> receiver) {
        //TODO
    }

    @Override
    public Void send(Message data) {
        return null;
    }
}
