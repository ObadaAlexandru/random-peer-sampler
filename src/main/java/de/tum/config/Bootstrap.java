package de.tum.config;

import de.tum.sampling.entity.Peer;

import java.util.List;

/**
 *  Provides an initial set of Peers
 */
public interface Bootstrap {
    /**
     * @return returns the bootstrapped peers
     */
    List<Peer> getPeers();
}
