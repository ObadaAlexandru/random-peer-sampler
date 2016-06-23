package de.tum.sampling.service;

import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.NseEstimateMessage;
import de.tum.communication.protocol.messages.NseQueryMessage;
import de.tum.communication.service.CommunicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static de.tum.communication.protocol.MessageType.NSE_ESTIMATE;
/**
 * Created by Alexandru Obada on 22/05/16.
 */

/**
 * Monitors the NSE, queries once per round the NSE service.
 */
@Slf4j
@Service
public class NseHandlerImpl implements NseHandler {

    private ScheduledExecutorService scheduler;
    private CommunicationService communicationService;
    private Integer nse;
    private Integer standardDeviation;

    @Autowired
    public NseHandlerImpl(CommunicationService communicationService,
                          @Value("#{iniConfig.getRoundDuration()}") Integer exchangeRate) {
        this.communicationService = communicationService;
        this.communicationService.addReceiver(this, NSE_ESTIMATE);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new NseQueryTask(), 0, exchangeRate, TimeUnit.MILLISECONDS);
    }

    @Override
    public Optional<Integer> getNetworkSizeEstimation() {
        return Optional.ofNullable(nse);
    }

    @Override
    public Optional<Integer> getStandardDeviation() {
        return Optional.ofNullable(standardDeviation);
    }

    @Override
    public Optional<Message> receive(Message message) {
        NseEstimateMessage nseEstimateMessage = (NseEstimateMessage) message;
        nse = nseEstimateMessage.getEstimatedPeerNumber();
        standardDeviation = nseEstimateMessage.getEstimatedStandardDeviation();
        log.info("NSE parameters updated size={} deviation={}", nse, standardDeviation);
        return Optional.empty();
    }

    private class NseQueryTask implements Runnable {
        @Override
        public void run() {
            Message nseQuery = new NseQueryMessage();
            communicationService.send(nseQuery);
            log.info("NSE query sent");
        }
    }
}
