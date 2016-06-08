package de.tum.communication.protocol;

import java.net.InetAddress;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;

import de.tum.common.exceptions.PeerDeserialisationException;
import de.tum.communication.protocol.messages.GossipNotificationMessage;
import de.tum.communication.protocol.messages.GossipNotifyMessage;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.NseEstimateMessage;
import de.tum.communication.protocol.messages.NseQueryMessage;
import de.tum.communication.protocol.messages.RpsQueryMessage;
import de.tum.communication.protocol.messages.RpsViewMessage;
import de.tum.sampling.entity.Peer;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Component
public class ProtocolImpl implements Protocol {
    private static final int IPV4_ADDRESS_SIZE = 4;
    private static final int IPV6_ADDRESS_SIZE = 16;
    private static final int HOSTKEY_SIZE = 550;

    @Override
    public Message deserialize(List<Byte> data) {
        List<Byte> header = data.subList(0, Message.WORD_LENGTH);
        List<Byte> payload = data.subList(Message.WORD_LENGTH, data.size());
        MessageType messageType = getMessageType(header);
        return getMessage(messageType, getSize(header), payload);
    }

    private Message getMessage(MessageType messageType, short size, List<Byte> payload) {
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
            case RPS_VIEW:
                return getRpsView(size, payload);
            default:
                throw new IllegalArgumentException("Unsupported message type");
        }
    }

    private Message getGossipNotification(List<Byte> payload) {
        return GossipNotificationMessage.builder()
                .datatype(Ints.fromBytes(payload.get(4), payload.get(5), payload.get(6), payload.get(7)))
                .payload(() -> payload.subList(Message.WORD_LENGTH, payload.size())).build();
    }

    private NseEstimateMessage getNseEstimate(List<Byte> payload) {
        byte[] bytes = Bytes.toArray(payload.subList(0, Message.WORD_LENGTH));
        Integer nseEstimatedPeerNumber = Ints.fromByteArray(bytes);
        Integer nseDeviation = Ints.fromBytes(payload.get(4), payload.get(5), payload.get(6), payload.get(7));
        return NseEstimateMessage.builder()
                .estimatedStandardDeviation(nseDeviation)
                .estimatedPeerNumbers(nseEstimatedPeerNumber).build();
    }

    private RpsViewMessage getRpsView(short size, List<Byte> payload) {
        List<SerializablePeer> peers = new ArrayList<>();
        int cur = 0;

        try {
            // XXX: Where are this 4 bytes coming from?
            while (cur < size - 4) {
                int port = Shorts.fromBytes(payload.get(cur), payload.get(cur + 1));
                short version = Shorts.fromBytes(payload.get(cur + 2), payload.get(cur + 3));
                int addrsize = 0;
                switch (version) {
                case 4:
                    addrsize = IPV4_ADDRESS_SIZE;
                    break;
                case 6:
                    addrsize = IPV6_ADDRESS_SIZE;
                    break;
                default:
                    throw new Exception("Invalid address type!");
                }
                InetAddress address = InetAddress
                        .getByAddress(Bytes.toArray(payload.subList(cur + 4, cur + 4 + addrsize)));
                cur += Message.WORD_LENGTH + addrsize;
                PublicKey hostkey = KeyFactory.getInstance("RSA").generatePublic(
                        new X509EncodedKeySpec(Bytes.toArray(payload.subList(cur, cur + HOSTKEY_SIZE))));
                Peer peer = Peer.builder().port(port).address(address).hostkey(hostkey).build();
                peers.add(new SerializablePeer(peer));
                cur += HOSTKEY_SIZE;
            }
        } catch (Exception ex) {
            throw new PeerDeserialisationException();
        }

        return RpsViewMessage.builder().source(peers.remove(0)).peers(peers).build();
    }

    private MessageType getMessageType(List<Byte> header) {
        short messageType = Shorts.fromBytes(header.get(2), header.get(3));
        return MessageType.getType(messageType);
    }

    private short getSize(List<Byte> header) {
        return Shorts.fromBytes(header.get(0), header.get(1));
    }
}