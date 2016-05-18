import de.tum.communication.protocol.Message;
import de.tum.communication.service.Receiver;
import de.tum.communication.service.Server;
import de.tum.communication.service.rps.RpsServer;

import java.util.Optional;

public class Application {
    public static void main(String... args) throws Exception {
        Receiver<Message> messageReceiver = Optional::of;
        Server server = new RpsServer(8080);
        server.setReceiver(messageReceiver);
        server.start();
    }
}
