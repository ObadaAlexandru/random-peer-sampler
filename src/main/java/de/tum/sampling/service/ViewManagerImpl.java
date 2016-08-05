package de.tum.sampling.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.tum.sampling.entity.Peer;
import de.tum.sampling.entity.PeerType;
import de.tum.sampling.repository.PeerRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
@Slf4j
@Service
public class ViewManagerImpl implements ViewManager {

    private AtomicInteger dynamicViewSize;

    private Double alpha;

    private  Double beta;

    private Double gamma;

    private ScheduledExecutorService schedulingExecutor;

    private PeerRepository peerRepository;

    private  Sampler sampler;

    @Builder
    @Autowired
    public ViewManagerImpl(PeerRepository peerRepository,
                           Sampler sampler,
                           NseHandler nseHandler,
                           @Value("${rps.sampling.view.dynamic_size:30}") Integer dynamicViewSize,
                           @Value("${rps.sampling.view.alpha:0.45}") Double alpha,
                           @Value("${rps.sampling.view.beta:0.45}") Double beta,
                           @Value("${rps.sampling.view.gamma:0.1}") Double gamma,
                           @Value("#{iniConfig.getRoundDuration()}") Integer viewSizeUpdateRate) {
        this.peerRepository = peerRepository;
        this.sampler = sampler;
        this.dynamicViewSize = new AtomicInteger(dynamicViewSize);
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;

        schedulingExecutor = Executors.newSingleThreadScheduledExecutor();
        schedulingExecutor.scheduleAtFixedRate(new ViewSizeUpdateTask(nseHandler), 0, viewSizeUpdateRate, TimeUnit.MILLISECONDS);
        log.info("View exchange scheduler started with exchange rate {}", viewSizeUpdateRate);
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
        return getRandom(peers, Math.round(alpha * dynamicViewSize.get()));
    }

    /**
     * Computes the new dynamic view as specified in Brahms
     */
    @Transactional
    @Override
    public void updateView() {
        List<Peer> pushed = peerRepository.deleteByPeerType(PeerType.PUSHED);
        List<Peer> pulled = peerRepository.deleteByPeerType(PeerType.PULLED);
        log.info("Received " + pushed.size() + " pushed peers.");
        log.info("Received " + pulled.size() + " pulled peers.");
        int viewSize = dynamicViewSize.get();
        double pushedLimit = alpha * viewSize;
        if (pushed.size() <= pushedLimit && pushed.size() > 0 && pulled.size() > 0) {
            pushed = getRandom(pushed, Math.round(pushedLimit));
            pulled = getRandom(pulled, Math.round(beta * viewSize));
            List<Peer> sampled = getRandom(peerRepository.getByPeerType(PeerType.SAMPLED), Math.round(gamma * viewSize));
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

    @lombok.Value
    private class ViewSizeUpdateTask implements Runnable {
        private NseHandler nseHandler;

        @Override
        public void run() {
            Optional<Integer> nse = nseHandler.getNetworkSizeEstimation();
            Optional<Integer> deviation = nseHandler.getStandardDeviation();
            if(nse.isPresent() && deviation.isPresent()) {
                Integer networkSize = nse.get();
                Integer sizeDeviation = deviation.get();
                Integer newSize = (int) Math.pow(networkSize + sizeDeviation, 1/3);
                if(newSize > dynamicViewSize.get()) {
                    dynamicViewSize.set(newSize);
                }
            }
        }
    }
}
