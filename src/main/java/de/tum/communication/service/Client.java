package de.tum.communication.service;

import de.tum.communication.protocol.messages.Message;

import java.net.SocketAddress;


/**
 * Created by Alexandru Obada on 12/05/16.
 */

/**
 * Communication interface to other modules
 */
public interface Client extends Sender<Message, Void>, ReceiverAware<Message> {
    void sendPersistent(Message data, SocketAddress address);
}