import de.tum.communication.service.rps.RpsServer;

public class Application {
    public static void main(String... args) throws Exception {
        new RpsServer(8080).start();
    }
}
