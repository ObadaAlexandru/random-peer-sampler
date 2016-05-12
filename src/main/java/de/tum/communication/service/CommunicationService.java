package de.tum.communication.service;

import de.tum.communication.protocol.Message;
import de.tum.communication.protocol.MessageType;

import java.util.List;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
public interface CommunicationService extends Receiver<List<Byte>>, Sender<Message> {
    void addReceiver(Receiver<? extends Message> messageReceiver, MessageType type);
    void send(Message message);
}