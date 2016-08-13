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
        SocketAddress socket = new InetSocketAddress("localhost", 9001);
        while (true){
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rpsTestClient.send(new RpsQueryMessage(), socket);
        }
    }
}
