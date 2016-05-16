package de.tum.communication.service;

import de.tum.communication.protocol.Message;


/**
 * Created by Alexandru Obada on 12/05/16.
 */

/**
 *  Communication interface to other modules
 */
public interface Client extends Sender<Message>, ReceiverAware<Message> {
}