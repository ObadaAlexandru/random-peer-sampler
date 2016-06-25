package de.tum.sampling.service;

import de.tum.sampling.entity.Peer;

import java.util.List;

/**
 * Created by Alexandru Obada on 13/06/16.
 */
public interface Sampler {
    void updateSample(List<Peer> peers);
    void setOffline(Peer peer);
    Peer getRandomPeer();
}
