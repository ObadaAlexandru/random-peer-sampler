package de.tum.sampling.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.tum.sampling.entity.Peer;
import de.tum.sampling.entity.PeerType;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
public interface PeerRepository extends JpaRepository<Peer, Long> {
    @Override
    List<Peer> findAll();
    List<Peer> getByPeerType(PeerType peerType);
    List<Peer> deleteByPeerType(PeerType peerType);
    void deleteById(Long id);
}