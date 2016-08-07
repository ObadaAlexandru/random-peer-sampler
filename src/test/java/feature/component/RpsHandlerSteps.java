package feature.component;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.RpsQueryMessage;
import de.tum.communication.service.Receiver;
import de.tum.sampling.repository.PeerRepository;
import de.tum.sampling.service.RpsTestClient;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static de.tum.sampling.entity.PeerType.DYNAMIC;
import static org.mockito.Mockito.mock;

/**
 * Created by Nicolas Frinker on 25/06/16.
 */
@Transactional
public class RpsHandlerSteps {

    @Autowired
    RpsTestClient testclient;

    @Value("#{iniConfig.getRPSPort()}")
    Integer rpsPort;

    @Value("#{iniConfig.getRPSHost()}")
    InetAddress rpsHost;

    @Value("#{iniConfig.getRoundDuration()}")
    private Integer roundDuration;

    private Receiver<Message> receiverMock;

    @Autowired
    PeerRepository peerRepository;


    @Given("^a running RPS service$")
    public void aRunningRpsService() throws InterruptedException {
        receiverMock = mock(Receiver.class);
        testclient.setReceiver(receiverMock);
        Thread.sleep(roundDuration);
    }

    @Given("^a running RPS service with no samples$")
    public void aRunningRpsServiceWithNoSamples() throws InterruptedException {
        receiverMock = mock(Receiver.class);
        testclient.setReceiver(receiverMock);
        peerRepository.deleteByPeerType(DYNAMIC);
    }

    @When("^the RPS service is queried$")
    public void theRpsServiceIsQueried() {
        SocketAddress socket = new InetSocketAddress(rpsHost, rpsPort);
        testclient.send(new RpsQueryMessage(), socket);
        try {
            Thread.sleep(roundDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Then("^the handler responds with a random peer message$")
    public void theHandlerRepondsWithARandomPeerMessage() {
        Mockito.verify(receiverMock, Mockito.timeout(1000)).receive(Mockito.any());
    }

    @Then("^the handler does not respond with a message$")
    public void theHandlerRepondsWithARandomPeerMessageed() {
        Mockito.verify(receiverMock, Mockito.times(0)).receive(Mockito.any());
    }
}
