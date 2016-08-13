package de.tum.communication.service;

import java.util.Optional;

/**
 * Created by Alexandru Obada on 12/05/16.
 */

/**
 * Receiver for a given type of message.
 */
public interface Receiver <T> {

    /**
     * Is called when we receive the specified type of message.
     *
     * @param message Incoming message
     * @return A message that shall be sent in response
     */
    Optional<T> receive(T message);
}