package de.tum.communication.service;

import java.net.SocketAddress;

/**
 * Created by Alexandru Obada on 12/05/16.
 */

/**
 * Sender for a certain type of data.
 */
public interface Sender <T, E> {
    E send(T data);
    E send(T data, SocketAddress address);
}
