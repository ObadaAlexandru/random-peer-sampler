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
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.RpsPushMessage;
import de.tum.communication.protocol.messages.RpsViewMessage;
import de.tum.communication.service.CommunicationService;
import de.tum.communication.service.Receiver;
import de.tum.sampling.entity.Peer;
import de.tum.sampling.entity.PeerType;
import de.tum.sampling.entity.SourcePeer;
import de.tum.sampling.repository.PeerRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PushPullHandler implements Receiver<Message> {
    private Random random = new Random();

    private CommunicationService communicationService;
    private PeerRepository peerRepository;
    private ViewManager viewManager;

    private Double pullfactor;
    private SourcePeer source;

    @Autowired
    public PushPullHandler(CommunicationService communicationService,
            ViewManager viewManager,
            SourcePeer source,
            PeerRepository peerRepository,
            @Value("${rps.sampling.pullfactor:0.10}") Double pullfactor) {
        this.communicationService = communicationService;
        this.viewManager = viewManager;
        this.peerRepository = peerRepository;
        this.pullfactor = pullfactor;
        this.source = source;

        communicationService.addReceiver(this, MessageType.RPS_PUSH);
        communicationService.addReceiver(this, MessageType.RPS_VIEW);
    }

    @Override
    public Optional<Message> receive(Message message) {

        if (message instanceof RpsViewMessage) {
            RpsViewMessage rpsviewmsg = (RpsViewMessage) message;
            rpsviewmsg.getPeers().stream().map(SerializablePeer::getPeer).forEach(p -> {
                p.setPeerType(PeerType.PULLED);
                peerRepository.save(p);
            });
            log.debug("Received view from peer " + rpsviewmsg.getPeers().get(0).getPeer());
        } else if (message instanceof RpsPushMessage) {
            // Collect pushed peers
            RpsPushMessage rpspushmsg = (RpsPushMessage) message;
            Peer peer = rpspushmsg.getPeer().getPeer();
            peer.setPeerType(PeerType.PUSHED);
            this.peerRepository.save(peer);

            // Reply to push with own view in some random cases
            if (random.nextDouble() < this.pullfactor) {
                RpsViewMessage viewmsg = RpsViewMessage.builder().source(new SerializablePeer(this.source))
                        .peers(viewManager.getForPush().stream().map(SerializablePeer::new)
                                .collect(Collectors.toCollection(ArrayList::new)))
                        .build();
                this.communicationService.send(viewmsg, new InetSocketAddress(peer.getAddress(), peer.getPort()));
                log.debug("Replied with view to peer " + peer);
            }
        }

        return null;
    }
}
