package de.tum.sampling.service;

import java.util.List;

import de.tum.sampling.entity.Peer;

/**
 * Created by Alexandru Obada on 13/06/16.
 *  Peer sampling service
 */
public interface Sampler {
    /**
     * Passes a set of peers through the sampler chain
     * @param peers
     */
    void updateSample(List<Peer> peers);

    /**
     * Set peer as non available
     * @param peer
     */
    void setOffline(Peer peer);

    /**
     * @return a random peer from the sampled set
     */
    Peer getRandomPeer();

    /**
     *  Reinitialize sampling chain
     */
    void clear();
}
