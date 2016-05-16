package de.tum.communication.service;

import de.tum.communication.protocol.Message;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
public interface Server extends ReceiverAware<Message>, Runnable {

    void start() throws Exception;

    void shutdown() throws Exception;
}
