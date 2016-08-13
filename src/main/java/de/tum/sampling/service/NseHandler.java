package de.tum.sampling.service;

import de.tum.communication.protocol.messages.Message;
import de.tum.communication.service.Receiver;

import java.util.Optional;
/**
 * Created by Alexandru Obada on 22/05/16.
 */

/**
 * Interface to Network Size Estimation service
 *
 * @see project specification 3.2.2
 */
public interface NseHandler extends Receiver<Message> {
    /**
     * Provides estimated network size
     * @return if size not available empty optional
     */
    Optional<Integer> getNetworkSizeEstimation();

    /**
     * Provides standard deviation
     * @return if value not available empty optional
     */
    Optional<Integer> getStandardDeviation();
}
