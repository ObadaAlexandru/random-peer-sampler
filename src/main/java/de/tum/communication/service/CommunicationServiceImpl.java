package de.tum.communication.service;

import de.tum.communication.protocol.Message;
import de.tum.communication.protocol.MessageType;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Service
public class CommunicationServiceImpl implements CommunicationService {

    private Map<MessageType, Receiver> receiver = new HashMap<>();

    @Autowired
    private Server server;

    @Autowired
    Map<MessageType, Sender> senders;

    @Override
    public void addReceiver(@NonNull Receiver<? extends Message> messageReceiver, @NonNull MessageType type) {
        receiver.put(type, messageReceiver);
    }

    @Override
    public void send(Message message) {
        Optional<Sender> sender = Optional.ofNullable(senders.get(message.getType()))
                .orElseThrow(() -> new IllegalArgumentException("Unknown message type"));
    }

    @Override
    public Optional<List<Byte>> receive(List<Byte> message) {
        return null;
    }
}
