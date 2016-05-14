package de.tum.communication.service;

import de.tum.communication.exceptions.UnknownMessageTypeException;
import de.tum.communication.protocol.ByteSerializable;
import de.tum.communication.protocol.Message;
import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.Protocol;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Slf4j
@Service
public class CommunicationServiceImpl implements CommunicationService {

    private Map<MessageType, Receiver<Message>> receivers = new HashMap<>();
    private Map<MessageType, Sender<Message>> senders = new HashMap<>();

    @Autowired
    private Server rpsServer;

    @Autowired
    private Protocol protocol;

    @Autowired
    public CommunicationServiceImpl(@Module(Module.Service.GOSSIP) Client gossipClient,
                                    @Module(Module.Service.NSE) Client nseClient) {
        nseClient.addReceiver(this);
        gossipClient.addReceiver(this);
        senders.put(MessageType.NSE_QUERY, nseClient);
        senders.put(MessageType.GOSSIP_ANNOUNCE, gossipClient);
        senders.put(MessageType.GOSSIP_NOTIFY, gossipClient);
    }

    @Override
    public void addReceiver(@NonNull Receiver<Message> messageReceiver, @NonNull MessageType type) {
        receivers.put(type, messageReceiver);
    }

    @Override
    public void send(@NonNull Message message) {
        Sender<? super Message> sender = Optional.ofNullable(senders.get(message.getType()))
                .orElseThrow(() -> new UnknownMessageTypeException(String.format("Message type <%s> not supported", message.getType())));
        log.info("Send message type {}", message.getType());
        sender.send(message);
    }

    @Override
    public Optional<List<Byte>> receive(@NonNull List<Byte> data) {
        Message message = protocol.deserialize(data);
        log.info("Received message type {}", message.getType());
        Receiver<Message> receiver = Optional.ofNullable(receivers.get(message.getType()))
                .orElseThrow(() -> new UnknownMessageTypeException(String.format("Message type <%s> not supported", message.getType())));
        return receiver.receive(message).map(ByteSerializable::getBytes);
    }
}
