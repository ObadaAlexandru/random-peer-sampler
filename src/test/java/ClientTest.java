import de.tum.communication.protocol.messages.NseQueryMessage;
import de.tum.communication.service.Client;
import de.tum.communication.service.clients.ClientImpl;
import de.tum.communication.service.clients.RpsClientWrapper;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class ClientTest {
    public static void main(String... args) throws UnknownHostException, InterruptedException {
        System.out.println("Connecting");
        ClientImpl client = new ClientImpl();
        Client gossipClientWrapper = new RpsClientWrapper(client);
        while (true) {
            System.out.println("Sending data...");
            gossipClientWrapper.send(new NseQueryMessage(), new InetSocketAddress(InetAddress.getByName("localhost"), 25005));
            Thread.sleep(1000);
        }
    }
}
