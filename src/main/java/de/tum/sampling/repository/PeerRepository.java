package de.tum.sampling.repository;

import de.tum.sampling.entity.Peer;
import de.tum.sampling.entity.PeerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
public interface PeerRepository extends JpaRepository<Peer, Long> {
    List<Peer> findAll();
    List<Peer> getByPeerType(PeerType peerType);
    List<Peer> deleteByPeerType(PeerType peerType);
    Peer deleteById(Long id);
}