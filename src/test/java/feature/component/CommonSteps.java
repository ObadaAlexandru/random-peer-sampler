package feature.component;

import com.google.common.primitives.Bytes;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import de.tum.Application;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.NseEstimateMessage;
import de.tum.sampling.repository.PeerRepository;
import de.tum.sampling.service.NseTestServer;
import feature.common.TestEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
@SpringApplicationConfiguration(Application.class)
@ContextConfiguration(classes = ComponentTests.TestConfiguration.class)
public class CommonSteps {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private TestEnvironment testEnvironment;

    @Autowired
    private PeerRepository repository;

    private NseTestServer testNseServer;

    @Value("#{iniConfig.getNseHost()}")
    private InetAddress nseHost;

    @Value("#{iniConfig.getNsePort()}")
    private Integer nsePort;

    @Before
    public void setUp() throws IOException {
        /**
         * Clean db before test
         */
        repository.findAll().forEach(peer -> repository.delete(peer));
        Message message = NseEstimateMessage.builder().estimatedPeerNumbers(15).estimatedStandardDeviation(1).build();
        testNseServer = new NseTestServer(nsePort, Bytes.toArray(message.getBytes()));
        Thread nseServerThread = new Thread(() -> {
            try {
                testNseServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        nseServerThread.start();
    }

    @After
    public void tearDown() {
        testNseServer.stop();
        ((ConfigurableApplicationContext)context).close();
    }
}
