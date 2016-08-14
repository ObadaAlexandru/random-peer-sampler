package de.tum.sampling.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.tum.sampling.entity.Peer;
import de.tum.sampling.entity.PeerType;

/**
 * Created by Alexandru Obada on 22/05/16.
 *
 * Database operations on {@link Peer} entity
 * Implementation provided by SpringData
 */
public interface PeerRepository extends JpaRepository<Peer, Long> {
    /**
     * Reads all the {@link Peer} present in the database
     * @return all the peers
     */
    @Override
    List<Peer> findAll();

    /**
     * Lookup peers by type
     * @param peerType
     * @return all the peers of the specified type
     */
    List<Peer> getByPeerType(PeerType peerType);

    /**
     * Removes all the present {@link Peer} instances
     * @param peerType
     * @return the removed peers
     */
    List<Peer> deleteByPeerType(PeerType peerType);

    /**
     * Removes a peer given the id
     * @param id
     */
    void deleteById(Long id);
}