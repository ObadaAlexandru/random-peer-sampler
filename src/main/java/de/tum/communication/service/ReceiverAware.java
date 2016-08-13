package de.tum.communication.service;

/**
 * Created by Alexandru Obada on 16/05/16.
 */

/**
 * Allows to add receivers.
 */
public interface ReceiverAware <T> {
    void setReceiver(Receiver<T> receiver);
}
