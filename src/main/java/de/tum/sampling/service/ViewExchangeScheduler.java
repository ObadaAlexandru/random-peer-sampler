package de.tum.sampling.service;

import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.SerializablePeer;
import de.tum.communication.protocol.messages.GossipAnnounceMessage;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.RpsViewMessage;
import de.tum.communication.service.CommunicationService;
import de.tum.config.HostKeyReader;
import de.tum.sampling.entity.Peer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Alexandru Obada on 30/05/16.
 */

/**
 *  Periodically announces the partial view to the other peers
 */
@Slf4j
@Service
public class ViewExchangeScheduler {

    private ScheduledExecutorService schedulingExecutor;

    private ViewManager viewManager;

    private CommunicationService communicationService;

    private HostKeyReader hostKeyReader;

    private InetAddress rpsHost;
    private Integer rpsPort;

    private Peer source;

    @Autowired
    @Builder
    public ViewExchangeScheduler(ViewManager viewManager,
                                 CommunicationService communicationService,
                                 HostKeyReader hostKeyReader,
                                 @Value("#{iniConfig.getRoundDuration()}") Integer exchangeRate,
                                 @Value("#{iniConfig.getRPSHost()}") InetAddress rpsHost,
                                 @Value("#{iniConfig.getRPSPort()}") Integer rpsPort) {
        this.viewManager = viewManager;
        this.communicationService = communicationService;
        this.hostKeyReader = hostKeyReader;
        this.rpsPort = rpsPort;
        this.rpsHost = rpsHost;
        schedulingExecutor = Executors.newSingleThreadScheduledExecutor();
        schedulingExecutor.scheduleAtFixedRate(new ViewExchangeTask(), 0, exchangeRate, TimeUnit.MILLISECONDS);
        log.info("View exchange scheduler started with exchange rate {}", exchangeRate);
    }

    @PreDestroy
    private void shutdown() {
        log.info("View exchange scheduler releasing resources");
        schedulingExecutor.shutdown();
    }

    private SerializablePeer getSource() {
        if(source == null) {
            source = Peer.builder().address(rpsHost).port(rpsPort).hostkey(hostKeyReader.getPublicKey()).build();
        }
        return new SerializablePeer(source);
    }

    private class ViewExchangeTask implements Runnable {
        @Override
        public void run() {
            Set<Peer> peers = viewManager.getPeers();
            List<SerializablePeer> serializablePeers = peers.stream().map(SerializablePeer::new).collect(Collectors.toList());
            Message viewMessage = RpsViewMessage.builder().source(getSource()).peers(serializablePeers).build();
            Message gossipAnnounce = GossipAnnounceMessage.builder()
                    .payload(viewMessage)
                    .ttl((short)255)
                    .datatype(MessageType.RPS_VIEW.getValue())
                    .payload(viewMessage).build();
             communicationService.send(gossipAnnounce);
            log.info("View has been pushed");
        }
    }
}
