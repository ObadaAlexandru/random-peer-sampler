package feature.component;

import com.google.common.primitives.Bytes;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.NseEstimateMessage;
import de.tum.sampling.service.NseHandler;
import de.tum.sampling.service.NseTestServer;
import feature.common.NseEstimationTest;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by Alexandru Obada on 23/06/16.
 */
public class NseHandlerSteps {
    @Autowired
    private NseTestServer nseTestServer;

    @Value("#{iniConfig.getNsePort()}")
    private Integer nsePort;

    @Value("#{iniConfig.getRoundDuration()}")
    private Integer roundDuration;

    @Autowired
    NseHandler nseHandler;

    private static final int DEFAULT_WAIT_ROUNDS = 3;

    private int nseQueriesReceived;

    private byte[] nseResponseAfterDownPeriod;

    @Before
    public void setUp() throws InterruptedException {
        stopNseServer();
    }

    @After
    public void tearDown() {
        nseTestServer.stop();
    }

    @Given("^that the NSE server is running on the configured port$")
    public void thatTheNSEServerIsRunningOnPort() {
        nseTestServer.setPort(nsePort);
    }

    @And("^that it responds as follows:$")
    public void thatItRespondsAsFollows(List<NseEstimationTest> data) {
        NseEstimationTest estimationData = data.get(0);
        Message nseMessage = NseEstimateMessage.builder()
                .estimatedPeerNumbers(estimationData.getNetworkEstimatedSize())
                .estimatedStandardDeviation(estimationData.getStandardDeviation())
                .build();
        nseTestServer.setResponse(Bytes.toArray(nseMessage.getBytes()));
    }

    @And("^that after the down time it responds as follows:$")
    public void thatAfterTheDownTimeItRespondsAsFollows(List<NseEstimationTest> data) {
        NseEstimationTest estimationData = data.get(0);
        Message nseMessage = NseEstimateMessage.builder()
                .estimatedPeerNumbers(estimationData.getNetworkEstimatedSize())
                .estimatedStandardDeviation(estimationData.getStandardDeviation())
                .build();
        nseResponseAfterDownPeriod = Bytes.toArray(nseMessage.getBytes());
    }

    @When("^the nse module goes down$")
    public void theNseModuleGoesDown() throws InterruptedException {
        nseTestServer.resetQueryCounter();
        runNseServer(DEFAULT_WAIT_ROUNDS);
        wait(DEFAULT_WAIT_ROUNDS);
    }

    @When("^nse handler is queried after (\\d+) rounds$")
    public void nseHandlerIsQueriedAfterRounds(int waitRounds) throws InterruptedException {
        nseTestServer.resetQueryCounter();
        runNseServer(waitRounds);
        nseQueriesReceived = nseTestServer.getNumReceivedQueries();
    }

    @When("^the nse module goes down for (\\d+) rounds$")
    public void theNseModuleGoesDownAfterRounds(int downRounds) throws InterruptedException {
        nseTestServer.resetQueryCounter();
        runNseServer(DEFAULT_WAIT_ROUNDS);
        wait(downRounds);
        nseTestServer.setResponse(nseResponseAfterDownPeriod);
        runNseServer(DEFAULT_WAIT_ROUNDS);
    }

    private void runNseServer(int numRounds) throws InterruptedException {
        stopNseServer();
        nseTestServer.start();
        wait(numRounds);
        stopNseServer();
    }

    private void stopNseServer() throws InterruptedException {
        if (nseTestServer.isRunning()) {
            nseTestServer.stop();
            while (nseTestServer.isRunning()) {
                Thread.sleep(200);
            }
        }
    }

    @Then("^the handler responds as follows:$")
    public void itRespondsAsFollows(List<NseEstimationTest> data) {
        NseEstimationTest expectedData = data.get(0);
        Optional<Integer> estimationOptional = nseHandler.getNetworkSizeEstimation();
        Optional<Integer> standardDeviationOptional = nseHandler.getStandardDeviation();
        assertThat(estimationOptional.isPresent()).isTrue();
        assertThat(standardDeviationOptional.isPresent()).isTrue();
        assertThat(estimationOptional.get()).isEqualTo(expectedData.getNetworkEstimatedSize());
        assertThat(standardDeviationOptional.get()).isEqualTo(expectedData.getStandardDeviation());
    }

    @And("^the nse module has received at least (\\d+) nse queries$")
    public void theNseModuleHasReceivedAtLeastNseQueries(int expectedNseQueries) {
        assertThat(nseQueriesReceived).isAtLeast(expectedNseQueries);
    }

    private void wait(int numRounds) {
        try {
            System.out.println("Waiting for three rounds");
            Thread.sleep(numRounds * roundDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
