package de.tum.config;

import de.tum.sampling.entity.Peer;

import java.util.List;

/**
 *  Provides an initial set of Peers
 */
public interface Bootstrap {
    List<Peer> getPeers();
}
