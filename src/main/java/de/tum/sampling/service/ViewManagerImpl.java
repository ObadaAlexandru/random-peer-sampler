package de.tum.sampling.service;

import de.tum.sampling.entity.Peer;
import de.tum.sampling.repository.PeerRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
@Service
public class ViewManagerImpl implements ViewManager {

    @Value("${rps.sampling.view.size:30}")
    @Getter
    @Setter
    private Integer viewSize;

    @Value("${rps.sampling.view.maxAge:30}")
    @Getter
    @Setter
    private Integer maxPeerAge;

    @Autowired
    private PeerRepository peerRepository;

    @Override
    public void merge(Set<Peer> incoming) {
        Set<Peer> currentPeers = peerRepository.findAll().stream().collect(Collectors.toSet());
        resetPeerAge(currentPeers, incoming);
        Set<Peer> merged = mergePeers(currentPeers, incoming);
        removeExtraPeers(merged, currentPeers);
        merged.forEach(peerRepository::save);
    }

    @Override
    public Set<Peer> getPeers() {
        return peerRepository.findAll().stream().collect(Collectors.toSet());
    }

    /**
     * Merge incoming with current view
     * from the current set just the peers with age lower than max age
     * are considered
     * @return Merge peer set
     */
    private Set<Peer> mergePeers(Set<Peer> current, Set<Peer> incoming) {
        return Stream.concat(current.stream(), incoming.stream())
                .filter(peer -> peer.getAge() < maxPeerAge)
                .sorted((p1, p2) -> p1.getAge().compareTo(p2.getAge()))
                .limit(viewSize)
                .collect(Collectors.toSet());
    }

    /**
     * Remove old peers from the current view to not overflow it
     *
     * @param merged  Merged incoming and current view, truncated at viewSize
     * @param current Current network view
     */
    private void removeExtraPeers(Set<Peer> merged, Set<Peer> current) {
        current.forEach(peer -> {
            if (!merged.contains(peer)) {
                peerRepository.delete(peer);
            }
        });
    }

    /**
     * Refresh the age of the peers that are currently in the local partial view
     * and also in the incoming partial view
     *
     * @param incoming Incoming network view
     * @param current  Current network view
     */
    private void resetPeerAge(Set<Peer> current, Set<Peer> incoming) {
        current.forEach(peer -> {
            if (incoming.contains(peer)) {
                peer.resetAge();
            }
        });
    }
}
