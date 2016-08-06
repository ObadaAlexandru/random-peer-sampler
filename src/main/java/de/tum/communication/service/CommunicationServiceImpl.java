package de.tum.communication.service;

import de.tum.communication.exceptions.UnknownMessageTypeException;
import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.messages.Message;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Slf4j
@Service
@Value
public class CommunicationServiceImpl implements CommunicationService {
    private ExecutorService communicationExecutor = Executors.newFixedThreadPool(3);
    private Map<MessageType, Receiver<Message>> receivers = new HashMap<>();
    private Map<MessageType, Sender<Message, Void>> senders = new HashMap<>();
    private Server rpsServer;

    @Autowired
    public CommunicationServiceImpl(@Module(Module.Service.GOSSIP) Client gossipClient,
                                    @Module(Module.Service.NSE) Client nseClient,
                                    @Module(Module.Service.RPS) Client rpsClient,
                                    Server rpsServer) {
        nseClient.setReceiver(this);
        gossipClient.setReceiver(this);
        rpsServer.setReceiver(this);
        this.rpsServer = rpsServer;
        senders.put(MessageType.NSE_QUERY, nseClient);
        senders.put(MessageType.GOSSIP_ANNOUNCE, gossipClient);
        senders.put(MessageType.GOSSIP_NOTIFY, gossipClient);
        senders.put(MessageType.GOSSIP_VALIDATION, gossipClient);
        communicationExecutor.submit(rpsServer);
    }

    @Override
    public void addReceiver(@NonNull Receiver<Message> messageReceiver, @NonNull MessageType type) {
        receivers.put(type, messageReceiver);
    }

    @Override
    public Future<Void> send(@NonNull Message message) {
        Sender<Message, Void> sender = Optional.ofNullable(senders.get(message.getType()))
                .orElseThrow(() -> new UnknownMessageTypeException(String.format("Message type <%s> not supported", message.getType())));
        log.info("Send message type {}", message.getType());
        return communicationExecutor.submit(() -> {
            sender.send(message);
            return null;
        });
    }

    @Override
    public Future<Void> send(Message message, SocketAddress address) {
        Sender<Message, Void> sender = Optional.ofNullable(senders.get(message.getType()))
                .orElseThrow(() -> new UnknownMessageTypeException(String.format("Message type <%s> not supported", message.getType())));
        log.info("Send message type {}", message.getType());
        return communicationExecutor.submit(() -> {
            sender.send(message, address);
            return null;
        });
    }

    @Override
    public Optional<Message> receive(@NonNull Message message) {
        log.info("Received message type {}", message.getType());
        Receiver<Message> receiver = Optional.ofNullable(receivers.get(message.getType()))
                .orElseThrow(() -> new UnknownMessageTypeException(String.format("Message type <%s> not supported", message.getType())));
        return receiver.receive(message);
    }

    @PreDestroy
    private void shutdown() {
        log.info("Communication service shutting down");
        communicationExecutor.shutdown();
    }
}
