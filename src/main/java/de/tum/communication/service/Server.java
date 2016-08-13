package de.tum.communication.service;

import de.tum.communication.protocol.messages.Message;

/**
 * Created by Alexandru Obada on 12/05/16.
 */

/**
 * Defines a simple server interface that allows starting and stopping server
 * Server also extends ReceiverAware to allow addition of receivers for
 * incoming messages.
 */
public interface Server extends ReceiverAware<Message>, Runnable {

    /**
     * Start server
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * Shut down server
     * @throws Exception
     */
    void shutdown() throws Exception;
}
