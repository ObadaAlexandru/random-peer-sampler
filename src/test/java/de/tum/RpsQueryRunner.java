package de.tum;

import de.tum.communication.protocol.messages.RpsQueryMessage;
import de.tum.communication.service.clients.ClientImpl;
import de.tum.sampling.service.RpsTestClient;
import de.tum.sampling.service.RpsTestPeerReceiver;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by Alexandru Obada on 08/08/16.
 */
public class RpsQueryRunner {
    public static void main(String... args) {
        RpsTestClient rpsTestClient = new RpsTestClient(new ClientImpl());
        RpsTestPeerReceiver receiver = new RpsTestPeerReceiver();
        rpsTestClient.setReceiver(receiver);
        SocketAddress socket = new InetSocketAddress("localhost", 7005);
        for(int i=0; i<100; i++) {
            rpsTestClient.send(new RpsQueryMessage(), socket);
        }
    }
}
