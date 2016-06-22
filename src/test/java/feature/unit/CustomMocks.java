package feature.unit;

import com.google.common.io.BaseEncoding;
import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.SerializablePeer;
import de.tum.communication.protocol.messages.Message;
import de.tum.sampling.entity.Peer;
import feature.common.TestPeer;
import org.mockito.Mockito;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomMocks {
    public static Message getMessage(MessageType messageType) {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getType()).thenReturn(messageType);
        Mockito.when(message.getBytes()).thenReturn(new ArrayList<>());
        return message;
    }

    public static SerializablePeer getPeer(String address, short port, String hostkey) throws UnknownHostException {
        Peer peer = Peer.builder()
                .address(InetAddress.getByName(address))
                .hostkey(getHostkey(hostkey))
                .port((int) port).build();
        return new SerializablePeer(peer);
    }

    public static PublicKey getHostkey(String hostkey) {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(BaseEncoding.base64().decode(hostkey)));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid key specification!");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("RSA not available!");
        }
    }

    public static Set<Peer> testPeerToPeer(List<TestPeer> testPeers) {
        return testPeers.stream().map(testPeer -> {
            try {
                return Peer.builder()
                        .address(InetAddress.getByName(testPeer.getAddress()))
                        .hostkey(CustomMocks.getHostkey(testPeer.getHostkey()))
                        .port(testPeer.getPort())
                        .build();
            } catch (UnknownHostException e) {
                throw new RuntimeException("Unknown host");
            }
        }).collect(Collectors.toSet());
    }
}
