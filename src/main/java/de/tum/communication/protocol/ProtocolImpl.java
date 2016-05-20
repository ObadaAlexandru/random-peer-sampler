package de.tum.communication.protocol;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Component
public class ProtocolImpl implements Protocol {
    @Override
    public Message deserialize(List<Byte> data) {
        List<Byte> header = data.subList(0, Message.WORD_LENGTH);
        List<Byte> payload = data.subList(Message.WORD_LENGTH, data.size());
        MessageType messageType = getMessageType(header);
        return getMessage(messageType, payload);
    }

    private Message getMessage(MessageType messageType, List<Byte> payload) {
        switch (messageType) {
            case RPS_QUERY:
                return new RpsQueryMessage();
            case NSE_QUERY:
                return new NseQueryMessage();
            case NSE_ESTIMATE:
                return getNseEstimate(payload);
            case GOSSIP_NOTIFY:
                return new GossipNotifyMessage();
            case GOSSIP_NOTIFICATION:
                return getGossipNotification(payload);
            default:
                throw new IllegalArgumentException("Unsupported message type");
        }
    }

    private Message getGossipNotification(List<Byte> payload) {
        return GossipNotificationMessage.builder()
                .datatype(Ints.fromBytes(payload.get(4), payload.get(5), payload.get(6), payload.get(7)))
                .payload(new ByteSerializable() {
                    @Override
                    public List<Byte> getBytes() {
                        return payload.subList(Message.WORD_LENGTH, payload.size());
                    }
                }).build();
    }

    private NseEstimateMessage getNseEstimate(List<Byte> payload) {
        byte[] bytes = Bytes.toArray(payload.subList(0, Message.WORD_LENGTH));
        Integer nseEstimatedPeerNumber = Ints.fromByteArray(bytes);
        Integer nseDeviation = Ints.fromBytes(payload.get(4), payload.get(5), payload.get(6), payload.get(7));
        return NseEstimateMessage.builder()
                .estimatedStandardDeviation(nseDeviation)
                .estimatedPeerNumbers(nseEstimatedPeerNumber).build();
    }

    private MessageType getMessageType(List<Byte> header) {
        short messageType = Shorts.fromBytes(header.get(2), header.get(3));
        return MessageType.getType(messageType);
    }
}