package de.tum.sampling.service;

import de.tum.sampling.entity.Peer;
import de.tum.sampling.entity.PeerType;
import de.tum.sampling.repository.PeerRepository;
import lombok.Builder;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
@Service
public class ViewManagerImpl implements ViewManager {

    @Value("${rps.sampling.view.dynamic_size:30}")
    private Integer dynamicViewSize;

    @Value("${rps.sampling.view.alpha:0.45}")
    private Double alpha;

    @Value("${rps.sampling.view.beta:0.45}")
    private  Double beta;

    @Value("${rps.sampling.view.gamma:0.1}")
    private Double gamma;

    @Autowired
    private PeerRepository peerRepository;

    @Autowired
    private  Sampler sampler;

    @Builder
    @Autowired
    public ViewManagerImpl(PeerRepository peerRepository,
                           Sampler sampler,
                           Integer dynamicViewSize,
                           Double alpha,
                           Double beta,
                           Double gamma) {
        this.peerRepository = peerRepository;
        this.sampler = sampler;
        this.dynamicViewSize = dynamicViewSize;
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
    }

    /**
     * Randomly selects a subset of the current dynamic view
     * According to Brahms this implements Limited pushes
     * @return a subset of the dynamic view
     */
    @Override
    public List<Peer> getForPush() {
        List<Peer> peers = peerRepository.getByPeerType(PeerType.DYNAMIC);
        Collections.shuffle(peers);
        return getRandom(peers, Math.round(alpha * dynamicViewSize));
    }

    /**
     * Computes the new dynamic as specified in Brahms
     */
    @Transactional
    @Override
    public void updateView() {
        List<Peer> pushed = peerRepository.deleteByPeerType(PeerType.PUSHED);
        List<Peer> pulled = peerRepository.deleteByPeerType(PeerType.PULLED);
        double pushedLimit = alpha * dynamicViewSize;
        if (pushed.size() <= pushedLimit && pushed.size() > 0 && pulled.size() > 0) {
            pushed = getRandom(pushed, Math.round(pushedLimit));
            pulled = getRandom(pulled, Math.round(beta * dynamicViewSize));
            List<Peer> sampled = getRandom(peerRepository.getByPeerType(PeerType.SAMPLED), Math.round(gamma * dynamicViewSize));
            List<Peer> newDynamicView = Stream.of(pushed, pulled, sampled)
                    .flatMap(List::stream)
                    .peek(peer -> peer.setPeerType(PeerType.DYNAMIC))
                    .collect(Collectors.toList());
            peerRepository.deleteByPeerType(PeerType.DYNAMIC);
            peerRepository.save(newDynamicView);
        }
        sampler.updateSample(Stream.concat(pushed.stream(), pulled.stream()).collect(Collectors.toList()));
    }

    private List<Peer> getRandom(List<Peer> peers, long n) {
        Collections.shuffle(peers);
        return peers.stream().limit(n).collect(Collectors.toList());
    }
}
