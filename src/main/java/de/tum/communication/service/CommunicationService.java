package de.tum.communication.service;

import de.tum.communication.protocol.Message;
import de.tum.communication.protocol.MessageType;

import java.util.List;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
/**
 *  Message dispatcher
 *  Forwards the incoming messages to registered receivers
 *  Forwards the outgoing messages to the respective clients
 */
public interface CommunicationService extends Receiver<List<Byte>>, Sender<Message> {
    void addReceiver(Receiver<Message> messageReceiver, MessageType type);
}