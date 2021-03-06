package de.tum.communication.protocol;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;

import de.tum.common.exceptions.PeerDeserializationException;
import de.tum.communication.protocol.messages.GossipNotificationMessage;
import de.tum.communication.protocol.messages.GossipNotifyMessage;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.NseEstimateMessage;
import de.tum.communication.protocol.messages.NseQueryMessage;
import de.tum.communication.protocol.messages.RpsPeerMessage;
import de.tum.communication.protocol.messages.RpsPingMessage;
import de.tum.communication.protocol.messages.RpsPushMessage;
import de.tum.communication.protocol.messages.RpsQueryMessage;
import de.tum.communication.protocol.messages.RpsViewMessage;
import de.tum.sampling.entity.Peer;
import de.tum.sampling.entity.KeyValidator;

/**
 * Created by Alexandru Obada on 12/05/16.
 */

/**
 * This class is responsible for serializing outgoing and deserializing incoming
 * messages of all known kinds.
 */
@Component
public class ProtocolImpl implements Protocol {
    private static final int ADDR_OFFSET = 4;
    private static final int IPV4_ADDRESS_SIZE = 4;
    private static final int IPV6_ADDRESS_SIZE = 16;
    private static final int HOSTKEY_SIZE = 550;

    private KeyValidator validator;

    @Autowired
    public ProtocolImpl(KeyValidator validator) {
        this.validator = validator;
    }

    @Override
    public Message deserialize(List<Byte> data) {
        List<Byte> header = data.subList(0, Message.WORD_LENGTH);
        List<Byte> payload = data.subList(Message.WORD_LENGTH, data.size());
        MessageType messageType = getMessageType(header);
        return getMessage(messageType, getSize(header), payload);
    }

    /**
     * Convert given payload into a Message object of given type
     *
     * @param messageType
     * @param size
     * @param payload
     * @return Message object deserialized from payload
     */
    private Message getMessage(MessageType messageType, short size, List<Byte> payload) {
        switch (messageType) {
            case RPS_QUERY:
                return new RpsQueryMessage();
            case RPS_PEER:
                return getRpsPeer(payload);
            case RPS_PING:
                return getRpsPing(payload);
            case RPS_PUSH:
                return getRpsPush(payload);
            case NSE_QUERY:
                return new NseQueryMessage();
            case NSE_ESTIMATE:
                return getNseEstimate(payload);
            case GOSSIP_NOTIFY:
                return getGossipNotify(payload);
            case GOSSIP_NOTIFICATION:
                return getGossipNotification(payload);
            case RPS_VIEW:
                return getRpsView(size, payload);
            default:
                throw new IllegalArgumentException("Unsupported message type");
        }
    }

    /**
     * Convert payload to GossipNotifyMessage
     */
    private Message getGossipNotify(List<Byte> payload) {
        return GossipNotifyMessage.builder()
                .datatype(Shorts.fromBytes(payload.get(2), payload.get(3))).build();
    }

    /**
     * Convert payload to GossipNotificationMessage
     */
    private Message getGossipNotification(List<Byte> payload) {
        return GossipNotificationMessage.builder()
                .datatype(Shorts.fromBytes(payload.get(2), payload.get(3)))
                .messageId(Shorts.fromBytes(payload.get(4), payload.get(5)))
                .payload(() -> payload.subList(Message.WORD_LENGTH, payload.size())).build();
    }

    /**
     * Convert payload to NseEstimateMessage
     */
    private NseEstimateMessage getNseEstimate(List<Byte> payload) {
        byte[] bytes = Bytes.toArray(payload.subList(0, Message.WORD_LENGTH));
        Integer nseEstimatedPeerNumber = Ints.fromByteArray(bytes);
        Integer nseDeviation = Ints.fromBytes(payload.get(4), payload.get(5), payload.get(6), payload.get(7));
        return NseEstimateMessage.builder()
                .estimatedStandardDeviation(nseDeviation)
                .estimatedPeerNumbers(nseEstimatedPeerNumber).build();
    }

    /**
     * Convert payload to RpsViewMessage
     */
    private RpsViewMessage getRpsView(short size, List<Byte> payload) {
        List<SerializablePeer> peers = new ArrayList<>();
        int cur = 0;

        Token token = new Token(payload.subList(0, Token.TOKEN_LENGTH));
        size -= Token.TOKEN_LENGTH;
        payload = payload.subList(Token.TOKEN_LENGTH, payload.size());

        // XXX: Where are this 4 bytes coming from?
        while (cur < size - 4) {
            Peer peer = this.bytesToPeer(payload.subList(cur, size - 4));
            peers.add(new SerializablePeer(peer));
            cur += new SerializablePeer(peer).getBytes().size();
        }

        return RpsViewMessage.builder().token(token).source(peers.remove(0)).peers(peers).build();
    }

    /**
     * Get peer out of list of bytes
     *
     * @param payload
     * @return
     * @throws PeerDeserializationException
     */
    private Peer bytesToPeer(List<Byte> payload) throws PeerDeserializationException {
        int cur = 0;
        Peer peer = null;
        int port = Shorts.fromBytes(payload.get(cur), payload.get(cur + 1));
        int addrsize = this.getAddressSize(payload.subList(cur + 2, cur + ADDR_OFFSET));
        InetAddress address;
        try {
            address = InetAddress.getByAddress(Bytes.toArray(payload.subList(cur + ADDR_OFFSET, cur + ADDR_OFFSET + addrsize)));
        } catch (UnknownHostException e) {
            throw new PeerDeserializationException("Invalid address!");
        }
        cur += Message.WORD_LENGTH + addrsize;
        PublicKey hostkey;
        try {
            hostkey = KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(Bytes.toArray(payload.subList(cur, cur + HOSTKEY_SIZE))));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new PeerDeserializationException("Invalid hostkey!");
        }
        if (!validator.isValidPublicKey(hostkey)) {
            throw new PeerDeserializationException("Invalid hostkey for this application!");
        }
        peer = Peer.builder().port(port).address(address).hostkey(hostkey).build();
        return peer;
    }

    /**
     * Decode bits that specify, if address in message is IPv4 or IPv6 and
     * return size of the address in the message
     *
     * @param payload
     * @return Size of address in payload
     */
    private int getAddressSize(List<Byte> payload) {
        int addrsize = 0;
        short version = Shorts.fromBytes(payload.get(0), payload.get(1));
        switch (version) {
        case 4:
            addrsize = IPV4_ADDRESS_SIZE;
            break;
        case 6:
            addrsize = IPV6_ADDRESS_SIZE;
            break;
        default:
            throw new PeerDeserializationException("Invalid address type!");
        }
        return addrsize;
    }

    /**
     * Convert payload to RpsPeerMessage
     */
    private RpsPeerMessage getRpsPeer(List<Byte> payload) {
        return RpsPeerMessage.builder().peer(new SerializablePeer(bytesToPeer(payload))).build();
    }

    /**
     * Convert payload to RpsPingMessage
     */
    private RpsPingMessage getRpsPing(List<Byte> payload) {
        return RpsPingMessage.builder().peer(new SerializablePeer(bytesToPeer(payload))).build();
    }

    /**
     * Convert payload to RpsPushMessage
     */
    private RpsPushMessage getRpsPush(List<Byte> payload) {
        return RpsPushMessage.builder().token(new Token(payload.subList(0, Token.TOKEN_LENGTH)))
                .peer(new SerializablePeer(bytesToPeer(payload.subList(Token.TOKEN_LENGTH, payload.size())))).build();
    }

    /**
     * Extract message type from header of packet
     *
     * @param header
     * @return Type of message
     */
    private MessageType getMessageType(List<Byte> header) {
        short messageType = Shorts.fromBytes(header.get(2), header.get(3));
        return MessageType.getType(messageType);
    }

    /**
     * Extract size from given header
     *
     * @param header
     * @return Size of packet encoded in header
     */
    private short getSize(List<Byte> header) {
        return Shorts.fromBytes(header.get(0), header.get(1));
    }
}