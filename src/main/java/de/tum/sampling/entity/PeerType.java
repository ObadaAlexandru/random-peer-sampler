package de.tum.sampling.entity;

/**
 * Created by Alexandru Obada on 12/06/16.
 *
 *  Peer statuses
 *  The {@link PeerType} is used to assign a specific {@link Peer} to one of the
 *  peer sets as described in the Brahms protocol (sampled, pulled, pushed, dynamic view)
 */
public enum PeerType {
    PULLED,
    PUSHED,
    SAMPLED,
    DYNAMIC
}
