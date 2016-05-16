package de.tum.communication.service;

import de.tum.communication.protocol.Message;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Service
@Module(Module.Service.NSE)
public class NseClient implements Client {
    @Override
    public void setReceiver(Receiver<Message> receiver) {
        //TODO
    }

    @Override
    public void send(Message data) {
        //TODO
    }
}
