package de.tum.sampling.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import de.tum.sampling.entity.PeerType;
import de.tum.sampling.repository.PeerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.SerializablePeer;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.RpsPeerMessage;
import de.tum.communication.service.CommunicationService;
import de.tum.sampling.entity.Peer;
import lombok.extern.slf4j.Slf4j;

import static de.tum.sampling.entity.PeerType.DYNAMIC;
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
    private PeerRepository peerRepository;
    private Random randomizer;

    @Autowired
    public RpsHandlerImpl(CommunicationService communicationService, PeerRepository peerRepository) {
        randomizer = new Random();
        communicationService.addReceiver(this, MessageType.RPS_QUERY);
        this.peerRepository = peerRepository;
    }

    @Override
    public Optional<Message> receive(Message message) {
        // We were asked for a random peer. Return one.
        log.info("Respond to RPS query.");
        List<Peer> dynamicView = peerRepository.getByPeerType(DYNAMIC);
        if(dynamicView != null) {
            Peer randomPeer = dynamicView.get(randomizer.nextInt(dynamicView.size()));
            return Optional.of(RpsPeerMessage.builder().peer(new SerializablePeer(randomPeer)).build());
        }
        return Optional.empty();
    }
}
