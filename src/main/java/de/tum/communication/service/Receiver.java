package de.tum.communication.service;

import java.util.Optional;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
public interface Receiver <T> {
    Optional<T> receive(T message);
}