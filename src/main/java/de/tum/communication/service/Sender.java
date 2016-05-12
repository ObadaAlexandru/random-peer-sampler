package de.tum.communication.service;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
public interface Sender <T> {
    void send(T data);
}
