package de.tum.sampling.service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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
import de.tum.sampling.entity.SourcePeer;
import lombok.Builder;

/**
 * Created by Alexandru Obada on 13/06/16.
 */
@Service
public class SamplerImpl implements Sampler, Receiver<Message> {
    private final Random prng = new Random();
    private final List<SamplingUnit> samplers = new ArrayList<>();
    private final CommunicationService communicationService;
    private final ScheduledExecutorService schedulingExecutor;
    private final int timeout;
    private final SourcePeer source;
    private final Set<Peer> pinged = new HashSet<>();

    @Builder
    @Autowired
    public SamplerImpl(CommunicationService comservice, @Value("#{iniConfig.getSamplerNum()}") Integer samplerNum,
            @Value("#{iniConfig.getValidationRate()}") Integer validationRate,
            @Value("#{iniConfig.getSamplerTimeout()}") Integer timeout,
            SourcePeer source) throws NoSuchAlgorithmException {
        this.communicationService = comservice;
        comservice.addReceiver(this, MessageType.RPS_PING);
        this.timeout = timeout;
        this.source = source;

        // Create given number of sampling units
        for (int i = 0; i < samplerNum; i++) {
            samplers.add(new SamplingUnit());
        }

        // Schedule validation of samples
        schedulingExecutor = Executors.newSingleThreadScheduledExecutor();
        schedulingExecutor.scheduleAtFixedRate(new ValidationSchedulerTask(), validationRate, validationRate, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateSample(List<Peer> peers) {
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
        }
    }

    @Override
    public Peer getRandomPeer() {
        if (this.samplers.size() > 0) {
            return this.samplers.get(this.prng.nextInt(this.samplers.size())).getPeer();
        }
        return null;
    }

    private class ValidationSchedulerTask implements Runnable {
        @Override
        public void run() {
            // Ping every peer once
            pinged.clear();
            for (SamplingUnit unit : samplers) {
                pinged.add(unit.sample());
            }

            for (Peer peer : pinged) {
                // Send ping message
                // XXX: This will not work yet and needs implementation!
                // XXX: Will be the same as for the rps pulls
                communicationService.send(new RpsPingMessage(new SerializablePeer(peer)));
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
            for (SamplingUnit unit : samplers) {
                if (pinged.contains(unit.getPeer())) {
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
        this.pinged.remove(pingmsg.getPeer());

        return Optional.empty();
    }
}
