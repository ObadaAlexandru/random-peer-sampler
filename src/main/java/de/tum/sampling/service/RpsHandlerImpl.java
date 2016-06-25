package de.tum.sampling.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.SerializablePeer;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.RpsPeerMessage;
import de.tum.communication.service.CommunicationService;
import lombok.extern.slf4j.Slf4j;
/**
 * Created by Nicolas Frinker on 25/06/16.
 */

/**
 * Implements the RPS service.
 * Responds to RPS query messages with a random peer wrapped in a RPS peer message.
 */
@Slf4j
@Service
public class RpsHandlerImpl implements RpsHandler {
    private final Sampler sampler;

    @Autowired
    public RpsHandlerImpl(CommunicationService communicationService, Sampler sampler) {
        this.sampler = sampler;
        communicationService.addReceiver(this, MessageType.RPS_QUERY);
    }

    @Override
    public Optional<Message> receive(Message message) {

        // We were asked for a random peer. Return one.
        log.info("Respond to RPS query.");
        Optional<Message> o = Optional.of(RpsPeerMessage.builder().peer(new SerializablePeer(sampler.getRandomPeer())).build());
        return o;
    }
}
