package de.tum.sampling.service;

import java.util.List;

import de.tum.sampling.entity.Peer;

/**
 * Created by Alexandru Obada on 13/06/16.
 */
public interface Sampler {
    void updateSample(List<Peer> peers);
    void setOffline(Peer peer);
    Peer getRandomPeer();
}
