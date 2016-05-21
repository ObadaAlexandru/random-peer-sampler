package de.tum.communication.service;

import de.tum.communication.protocol.Message;
import de.tum.communication.protocol.MessageType;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
/**
 *  Message dispatcher
 *  Forwards the incoming messages to registered receivers
 *  Forwards the outgoing messages to the respective clients
 */
public interface CommunicationService extends Receiver<Message>, Sender<Message, Future<Void>> {
    void addReceiver(Receiver<Message> messageReceiver, MessageType type);
}