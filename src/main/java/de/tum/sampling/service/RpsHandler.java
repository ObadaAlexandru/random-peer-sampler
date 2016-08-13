package de.tum.sampling.service;

import de.tum.communication.protocol.messages.Message;
import de.tum.communication.service.Receiver;
/**
 * Created by Nicolas Frinker on 25/06/16.
 */

/**
 * Handles the RPS queries
 * Replies with a Randomly selected {@link de.tum.sampling.entity.Peer} from the dynamic view
 */
public interface RpsHandler extends Receiver<Message> {

}
