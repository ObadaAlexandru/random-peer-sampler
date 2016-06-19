package de.tum.communication.service;

import lombok.NonNull;

import java.net.InetAddress;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
public interface Sender <T, E> {
    E send(T data);
    E send(T data, InetAddress peerAddress, Integer port);
}
