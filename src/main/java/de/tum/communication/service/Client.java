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

    /**
     * Open a persistent connection to given address and send given message on
     * it
     *
     * @param data
     * @param address
     */
    void sendPersistent(Message data, SocketAddress address);
}