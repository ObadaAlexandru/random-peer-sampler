package de.tum.sampling.service;

import de.tum.config.Bootstrap;
import de.tum.sampling.entity.Peer;
import de.tum.sampling.entity.PeerType;
import de.tum.sampling.repository.PeerRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.tum.sampling.entity.PeerType.DYNAMIC;

/**
 * Created by Alexandru Obada on 22/05/16.
 *
 */
@Slf4j
@Service
public class ViewManagerImpl implements ViewManager {

    private AtomicInteger dynamicViewSize;

    private Double alpha;

    private Double beta;

    private Double gamma;

    private Bootstrap bootstrap;

    private PeerRepository peerRepository;

    private Sampler sampler;

    @Builder
    @Autowired
    public ViewManagerImpl(PeerRepository peerRepository,
                           Sampler sampler,
                           Bootstrap bootstrap,
                           @Value("${rps.sampling.view.dynamic_size:30}") Integer dynamicViewSize,
                           @Value("${rps.sampling.view.alpha:0.45}") Double alpha,
                           @Value("${rps.sampling.view.beta:0.45}") Double beta,
                           @Value("${rps.sampling.view.gamma:0.1}") Double gamma) {
        this.peerRepository = peerRepository;
        this.sampler = sampler;
        this.bootstrap = bootstrap;
        this.dynamicViewSize = new AtomicInteger(dynamicViewSize);
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        initDynamicView();
    }

    @Override
    public void setViewSize(int viewSize) {
        this.dynamicViewSize.set(viewSize);
    }

    @Override
    public int getViewSize() {
        return this.dynamicViewSize.get();
    }

    private void initDynamicView() {
        List<Peer> dynamicView = peerRepository.getByPeerType(DYNAMIC);
        if (dynamicView.isEmpty()) {
            List<Peer> bootstrapped = bootstrap.getPeers();
            peerRepository.save(bootstrapped);
        }
    }


    @Override
    public List<Peer> getForPush() {
        List<Peer> peers = peerRepository.getByPeerType(DYNAMIC);
        Collections.shuffle(peers);
        return getRandom(peers, Math.round(alpha * dynamicViewSize.get()));
    }


    @Transactional
    @Override
    public void updateView() {
        List<Peer> pushed = peerRepository.deleteByPeerType(PeerType.PUSHED);
        List<Peer> pulled = peerRepository.deleteByPeerType(PeerType.PULLED);
        log.info("Received " + pushed.size() + " pushed peers.");
        log.info("Received " + pulled.size() + " pulled peers.");
        int viewSize = dynamicViewSize.get();
        double pushedLimit = alpha * viewSize;
        if (pushed.size() <= pushedLimit && !pushed.isEmpty() && !pulled.isEmpty()) {
            log.info("Update view with pushed and pulled peers");
            pushed = getRandom(pushed, Math.round(pushedLimit));
            pulled = getRandom(pulled, Math.round(beta * viewSize));
            List<Peer> sampled = getRandom(peerRepository.getByPeerType(PeerType.SAMPLED), Math.round(gamma * viewSize));
            List<Peer> newDynamicView = Stream.of(pushed, pulled, sampled)
                    .flatMap(List::stream)
                    .map(peer -> Peer.builder()
                            .address(peer.getAddress())
                            .port(peer.getPort())
                            .hostkey(peer.getHostkey())
                            .peerType(DYNAMIC)
                            .build())
                    .distinct()
                    .collect(Collectors.toList());
            peerRepository.deleteByPeerType(DYNAMIC);
            peerRepository.save(newDynamicView);
        }
        sampler.updateSample(Stream.concat(pushed.stream(), pulled.stream()).distinct().collect(Collectors.toList()));
    }

    private List<Peer> getRandom(List<Peer> peers, long n) {
        Collections.shuffle(peers);
        return peers.stream().limit(n).collect(Collectors.toList());
    }
}