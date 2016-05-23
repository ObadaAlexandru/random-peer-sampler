package de.tum.sampling.repository;

import de.tum.sampling.entity.Peer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
public interface PeerRepository extends JpaRepository<Peer, String> {
    List<Peer> findAll();
}