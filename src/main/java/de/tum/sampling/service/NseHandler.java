package de.tum.sampling.service;

import de.tum.communication.protocol.messages.Message;
import de.tum.communication.service.Receiver;

import java.util.Optional;
/**
 * Created by Alexandru Obada on 22/05/16.
 */

/**
 * Interface to Network Size Estimation service
 */
public interface NseHandler extends Receiver<Message> {
    Optional<Integer> getNetworkSizeEstimation();
    Optional<Integer> getStandardDeviation();
}
