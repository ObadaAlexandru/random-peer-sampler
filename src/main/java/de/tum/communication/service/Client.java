package de.tum.communication.service;

import de.tum.communication.protocol.Message;

import java.util.List;

/**
 * Created by Alexandru Obada on 12/05/16.
 */

/**
 *  Communication interface to other modules
 */
public interface Client extends Sender<Message> {
    void addReceiver(Receiver<List<Byte>> receiver);
}