package feature.unit;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.service.CommunicationService;
import de.tum.config.HostKeyReader;
import de.tum.sampling.service.ViewExchangeScheduler;
import de.tum.sampling.service.ViewManager;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.Collections;

import static org.mockito.Mockito.when;

/**
 * Created by Alexandru Obada on 31/05/16.
 */
public class ViewExchangeSteps {
    private Integer rpsPort;
    private InetAddress rpsHost;
    private Integer roundDuration;

    @Mock
    private CommunicationService communicationService;
    @Mock
    private ViewManager viewManager;
    @Mock
    private HostKeyReader hostKeyReader;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PublicKey mockPrivateKey = Mockito.mock(PublicKey.class);
        when(mockPrivateKey.getEncoded()).thenReturn(new byte[] {0, 1, 2, 3, 4 ,5});
        when(hostKeyReader.getPublicKey()).thenReturn(mockPrivateKey);
        when(viewManager.getForPush()).thenReturn(Collections.emptyList());
    }

    @Given("^that the rps port is \"([^\"]*)\"$")
    public void thatTheRpsPortIs(Integer rpsPort) {
        this.rpsPort = rpsPort;
    }

    @And("^that the rps host is \"([^\"]*)\"$")
    public void thatTheRpsHostIs(String rpsHost) throws UnknownHostException {
        this.rpsHost = InetAddress.getByName(rpsHost);
    }

    @And("^that the round duration is \"([^\"]*)\" milliseconds$")
    public void thatTheRoundDurationIsMilliseconds(Integer roundDuration) {
        this.roundDuration = roundDuration;
    }

    @When("^the view exchange scheduler starts$")
    public void theViewExchangeSchedulerStarts() {
        ViewExchangeScheduler.builder()
                .exchangeRate(roundDuration)
                .rpsHost(rpsHost)
                .rpsPort(rpsPort)
                .communicationService(communicationService)
                .viewManager(viewManager)
                .hostKeyReader(hostKeyReader).build();
    }

    @Then("^it periodically announces the view$")
    public void itPeriodicallyAnnouncesTheView() throws InterruptedException {
        Thread.sleep(roundDuration*3);
        Mockito.verify(communicationService, Mockito.atLeast(2)).send(Mockito.any(Message.class));
    }
}
