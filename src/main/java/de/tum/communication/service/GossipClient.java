package de.tum.communication.service;

import de.tum.communication.protocol.Message;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Service
@Module(Module.Service.GOSSIP)
public class GossipClient implements Client {
    @Override
    public void addReceiver(Receiver<List<Byte>> receiver) {
        //TODO
    }

    @Override
    public void send(Message data) {
        //TODO
    }
}
