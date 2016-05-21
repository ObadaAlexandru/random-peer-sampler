package de.tum.sampling.repository;

import de.tum.sampling.entity.Peer;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
public interface PeerRepository extends CrudRepository<Peer, String> {
}