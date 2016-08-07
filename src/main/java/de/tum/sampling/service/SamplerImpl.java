package de.tum.sampling.service;

import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.SerializablePeer;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.RpsPingMessage;
import de.tum.communication.service.CommunicationService;
import de.tum.communication.service.Receiver;
import de.tum.sampling.entity.Peer;
import de.tum.sampling.entity.PeerType;
import de.tum.sampling.entity.SourcePeer;
import de.tum.sampling.repository.PeerRepository;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Alexandru Obada on 13/06/16.
 */
@Service
@Slf4j
public class SamplerImpl implements Sampler, Receiver<Message> {
    private final List<SamplingUnit> samplers = new ArrayList<>();
    private final CommunicationService communicationService;
    private final ScheduledExecutorService schedulingExecutor;
    private final int timeout;
    private final SourcePeer source;
    private final Set<Peer> pinged = new HashSet<>();
    private final PeerRepository peerRepository;

    @Builder
    @Autowired
    public SamplerImpl(CommunicationService comservice, @Value("#{iniConfig.getSamplerNum()}") Integer samplerNum,
            @Value("#{iniConfig.getValidationRate()}") Integer validationRate,
            @Value("#{iniConfig.getSamplerTimeout()}") Integer timeout, SourcePeer source,
            PeerRepository peerRepository) throws NoSuchAlgorithmException {
        this.communicationService = comservice;
        comservice.addReceiver(this, MessageType.RPS_PING);
        this.timeout = timeout;
        this.peerRepository = peerRepository;
        this.source = source;

        // Create given number of sampling units
        log.info("Runnning " + samplerNum + " samplers.");
        for (int i = 0; i < samplerNum; i++) {
            samplers.add(new SamplingUnit(peerRepository));
        }

        // Schedule validation of samples
        schedulingExecutor = Executors.newSingleThreadScheduledExecutor();
        schedulingExecutor.scheduleAtFixedRate(new ValidationSchedulerTask(), validationRate, validationRate, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateSample(List<Peer> peers) {
        log.info("Update sample with " + peers.size() + " peers.");
        for (Peer peer : peers) {
            for (SamplingUnit unit : this.samplers) {
                unit.next(peer);
            }
        }
    }

    @Override
    public void setOffline(Peer peer) {
        for (SamplingUnit unit : this.samplers) {
            if (unit.getPeer().equals(peer)) {
                // Reinitialize sampler because peer is offline
                unit.init();
            }
            this.peerRepository.delete(peer);
        }
    }

    @Override
    public void clear() {
        for (SamplingUnit unit : this.samplers) {
            unit.init();
        }
    }

    @Override
    public Peer getRandomPeer() {
        Collections.shuffle(this.samplers);
        for (SamplingUnit unit : this.samplers) {
            Peer peer = unit.getPeer();
            if (peer != null)
                return peer;
        }
        return null;
    }

    private class ValidationSchedulerTask implements Runnable {
        @Override
        public void run() {
            log.info("Ping all sampled peers (timeout="+timeout+")");

            // Ping every peer once
            pinged.clear();
            for (SamplingUnit unit : samplers) {
                Peer peer = unit.sample();
                if (peer != null)
                    pinged.add(peer);
            }

            for (Peer peer : pinged) {

                // Make sure, we never ping our self (endless loop)
                if (peer.getAddress().getHostAddress().equals(source.getAddress().getHostAddress())
                        && (peer.getPort().equals(source.getPort()))) {
                    continue;
                }

                // Send ping message
                communicationService.send(new RpsPingMessage(new SerializablePeer(peer)), new InetSocketAddress(peer.getAddress(), peer.getPort()));
            }

            // Add timeout
            Executors.newSingleThreadScheduledExecutor().schedule(new ValidationTimeoutTask(), timeout,
                    TimeUnit.MILLISECONDS);
        }
    }

    private class ValidationTimeoutTask implements Runnable {
        @Override
        public void run() {
            // For all pinged peers that did not reply yet, invalidate sampler
            log.info("Found " + pinged.size() + " offline peers in sample list");
            for (SamplingUnit unit : samplers) {
                if (pinged.contains(unit.getPeer())) {
                    log.debug("Peer " + unit.getPeer() + " seems to be offline. Remove from sample list.");
                    unit.init();
                }
            }
            pinged.clear();
        }
    }

    @Override
    public Optional<Message> receive(Message message) {
        if (!(message instanceof RpsPingMessage))
            return Optional.empty();
        RpsPingMessage pingmsg = (RpsPingMessage) message;

        // If we receive a ping, reply back
        if (pingmsg.getPeer().getPeer().equals(this.source)) {
            return Optional.of(pingmsg);
        }

        // Peer replied, remove from pending list
        this.pinged.remove(pingmsg.getPeer().getPeer());

        return Optional.empty();
    }
}
