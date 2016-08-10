package de.tum.sampling.service;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.SerializablePeer;
import de.tum.communication.protocol.messages.GossipNotifyMessage;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.RpsPushMessage;
import de.tum.communication.protocol.messages.RpsViewMessage;
import de.tum.communication.service.CommunicationService;
import de.tum.communication.service.Receiver;
import de.tum.sampling.entity.Peer;
import de.tum.sampling.entity.PeerType;
import de.tum.sampling.entity.SourcePeer;
import de.tum.sampling.entity.TokenRepo;
import de.tum.sampling.repository.PeerRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Nicolas Frinker on 04/08/16.
 */

/**
 * Receive push messages and send a view back
 */
@Service
@Slf4j
public class PushPullHandler implements Receiver<Message> {
    private Random random = new Random();

    private CommunicationService communicationService;
    private PeerRepository peerRepository;
    private ViewManager viewManager;
    private TokenRepo tokenrepo;

    private Double pullratio;
    private SourcePeer source;

    @Autowired
    public PushPullHandler(CommunicationService communicationService,
            ViewManager viewManager,
            SourcePeer source,
            PeerRepository peerRepository,
            TokenRepo tokenrepo,
            @Value("#{iniConfig.getPullRatio()}") Double pullratio) {
        this.communicationService = communicationService;
        this.viewManager = viewManager;
        this.peerRepository = peerRepository;
        this.tokenrepo = tokenrepo;
        this.pullratio = pullratio;
        this.source = source;

        communicationService.addReceiver(this, MessageType.RPS_PUSH);
        communicationService.addReceiver(this, MessageType.RPS_VIEW);
        communicationService.send(new GossipNotifyMessage(MessageType.RPS_PUSH.getValue()));
    }

    /**
     * Set new pull ratio
     *
     * @param ratio
     */
    public void setPullratio(double ratio) {
        this.pullratio = ratio;
    }

    @Override
    public Optional<Message> receive(Message message) {
        if (message instanceof RpsViewMessage) {
            this.handleRpsView((RpsViewMessage) message);
        } else if (message instanceof RpsPushMessage) {
            this.handleRpsPush((RpsPushMessage) message);
        }
        return Optional.empty();
    }

    /**
     * Receive rps view message
     *
     * @param message
     * @return
     */
    private void handleRpsView(RpsViewMessage message) {

        // Validate incoming token
        if (!this.tokenrepo.checkToken(message.getToken())) {
            // Skip
            log.warn("Skipping view message with invalid token!");
            return;
        }

        // Save incoming peers
        Peer viewsourcepeer = message.getSource().getPeer();
        viewsourcepeer.setPeerType(PeerType.PULLED);
        peerRepository.save(viewsourcepeer);
        message.getPeers().stream().map(SerializablePeer::getPeer).forEach(p -> {
            p.setPeerType(PeerType.PULLED);
            peerRepository.save(p);
        });
        log.info("Received view containing " + message.getPeers().size() + " peers from peer "
                + message.getPeers().get(0).getPeer());
    }

    /**
     * Receive rps push message and send view in reply in some cases
     *
     * @param message
     * @return
     */
    private void handleRpsPush(RpsPushMessage message) {
        Peer peer = message.getPeer().getPeer();
        peer.setPeerType(PeerType.PUSHED);
        this.peerRepository.save(peer);
        log.info("Received push from " + peer);

        // Reply to push with own view in some random cases
        if (random.nextDouble() < this.pullratio) {
            RpsViewMessage viewMessage = RpsViewMessage.builder().token(message.getToken()).source(new SerializablePeer(this.source))
                    .peers(viewManager.getForPush().stream().map(SerializablePeer::new)
                            .collect(Collectors.toCollection(ArrayList::new)))
                    .build();
            this.communicationService.send(viewMessage, new InetSocketAddress(peer.getAddress(), peer.getPort()));
            log.info("Replied with view to peer " + peer);
        }
    }
}
