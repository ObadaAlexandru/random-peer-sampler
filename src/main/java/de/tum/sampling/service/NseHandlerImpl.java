package de.tum.sampling.service;

import static de.tum.communication.protocol.MessageType.NSE_ESTIMATE;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.NseEstimateMessage;
import de.tum.communication.protocol.messages.NseQueryMessage;
import de.tum.communication.service.CommunicationService;
import lombok.extern.slf4j.Slf4j;
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
    private ViewManager viewManager;
    private PushPullHandler pushPullHandler;
    private Integer nse;
    private Integer standardDeviation;

    @Autowired
    public NseHandlerImpl(CommunicationService communicationService,
                          ViewManager viewManager,
                          PushPullHandler pushPullHandler,
                          @Value("#{iniConfig.getRoundDuration()}") Integer exchangeRate) {
        this.communicationService = communicationService;
        this.communicationService.addReceiver(this, NSE_ESTIMATE);
        this.viewManager = viewManager;
        this.pushPullHandler = pushPullHandler;
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

        // Update view size
        Integer newSize = (int) Math.pow(nse + standardDeviation, 1/3);
        if(newSize > this.viewManager.getViewSize()) {
            this.viewManager.setViewSize(newSize);
            log.info("Set dynamic view size to " + newSize);
        }

        // Update pull ratio
        double pullratio = (nse > 10) ? Math.pow(nse-10, 1/2) : 1.0;
        this.pushPullHandler.setPullratio(pullratio);
        log.info("Set pull ratio to " + pullratio);

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
