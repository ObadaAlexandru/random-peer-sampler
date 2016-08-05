package de.tum.sampling.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.tum.communication.protocol.SerializablePeer;
import de.tum.communication.protocol.messages.GossipAnnounceMessage;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.RpsPushMessage;
import de.tum.communication.service.CommunicationService;
import de.tum.sampling.entity.SourcePeer;
import de.tum.sampling.entity.TokenRepo;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Alexandru Obada on 30/05/16.
 */

/**
 * Periodically pushes the identity of the current peer via Gossip
 */
@Slf4j
@Service
public class PushScheduler {
    private ScheduledExecutorService schedulingExecutor;
    private CommunicationService communicationService;
    private ViewManager viewManager;
    private SourcePeer source;
    private TokenRepo tokenrepo;

    @Autowired
    @Builder
    public PushScheduler(CommunicationService communicationService,
                         ViewManager viewManager,
                         SourcePeer source,
                         TokenRepo tokenrepo,
                         @Value("#{iniConfig.getRoundDuration()}") Integer exchangeRate) {
        this.communicationService = communicationService;
        this.viewManager = viewManager;
        this.source = source;
        this.tokenrepo = tokenrepo;
        schedulingExecutor = Executors.newSingleThreadScheduledExecutor();
        schedulingExecutor.scheduleAtFixedRate(new ViewExchangeTask(), 0, exchangeRate, TimeUnit.MILLISECONDS);
        log.info("View exchange scheduler started with exchange rate {}", exchangeRate);
    }

    @PreDestroy
    private void shutdown() {
        log.info("View exchange scheduler releasing resources");
        schedulingExecutor.shutdown();
    }

    private class ViewExchangeTask implements Runnable {
        @Override
        public void run() {

            // End current round and start a new one
            viewManager.updateView();
            log.info("New round has been started.");

            // Push current view via gossip
            Message viewMessage = new RpsPushMessage(new SerializablePeer(source), tokenrepo.newToken());
            Message gossipAnnounce = GossipAnnounceMessage.builder()
                    .payload(viewMessage)
                    .ttl((short) 255)
                    .payload(viewMessage)
                    .datatype(viewMessage.getType().getValue())
                    .build();
            communicationService.send(gossipAnnounce);
            log.info("Push message has been sent.");
        }
    }
}