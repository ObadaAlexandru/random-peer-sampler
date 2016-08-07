package de.tum.sampling.service;

import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.RpsPeerMessage;
import de.tum.communication.service.Receiver;

import java.util.Optional;

/**
 * Created by Alexandru Obada on 08/08/16.
 */
public class RpsTestPeerReceiver implements Receiver<Message> {
    @Override
    public Optional<Message> receive(Message message) {
        RpsPeerMessage rpsPeerMessage = (RpsPeerMessage) message;
        System.out.println(rpsPeerMessage.getPeer());
        return Optional.empty();
    }
}
