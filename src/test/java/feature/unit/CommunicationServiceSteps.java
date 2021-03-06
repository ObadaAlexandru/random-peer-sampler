package feature.unit;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.service.Client;
import de.tum.communication.service.CommunicationService;
import de.tum.communication.service.CommunicationServiceImpl;
import de.tum.communication.service.Module;
import de.tum.communication.service.Receiver;
import de.tum.communication.service.Server;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.mock;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
public class CommunicationServiceSteps {

    @InjectMocks
    private CommunicationService communicationService;

    private Client gossipMock;
    private Client nseMock;
    private Client rpsClient;

    private Server rpsServerMock;
    private MessageType messageType;
    private Receiver<Message> receiverMock;
    private Message messageMock;

    @Before
    public void setUp() {
        gossipMock = mock(Client.class);
        nseMock = mock(Client.class);
        rpsServerMock = mock(Server.class);
        rpsClient = mock(Client.class);
        communicationService = new CommunicationServiceImpl(gossipMock, nseMock, rpsClient, rpsServerMock);
        MockitoAnnotations.initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Given("^that the communication service has a receiver of \"([^\"]*)\"$")
    public void thatTheCommunicationServiceHasAReceiverOf(MessageType receivedMessageType) {
        receiverMock = (Receiver<Message>) mock(Receiver.class);
        messageMock = CustomMocks.getMessage(receivedMessageType);
        Mockito.when(receiverMock.receive(Mockito.any())).thenReturn(Optional.empty());
        communicationService.addReceiver(receiverMock, receivedMessageType);
    }

    @Given("^a message of type \"([^\"]*)\" has to be sent$")
    public void aMessageOfTypeHasToBeSent(MessageType messageType) {
        this.messageType = messageType;
    }

    @When("^the message is sent$")
    public void theMessageIsSent() throws ExecutionException, InterruptedException {
        messageMock = CustomMocks.getMessage(messageType);
        communicationService.send(messageMock).get();
    }

    @When("^the message is received$")
    public void theMessageIsReceived() {
        communicationService.receive(messageMock);
    }

    @Then("^it is forwarded to the client \"([^\"]*)\"$")
    public void itIsForwardedToTheClient(Module.Service serviceType) {
        if (Module.Service.NSE.equals(serviceType)) {
            Mockito.verify(nseMock, Mockito.timeout(1000)).send(messageMock);
            Mockito.verify(nseMock, Mockito.times(1)).send(messageMock);
        } else if (Module.Service.GOSSIP.equals(serviceType)) {
            Mockito.verify(gossipMock, Mockito.timeout(1000)).send(messageMock);
            Mockito.verify(gossipMock, Mockito.times(1)).send(messageMock);
        } else {
            throw new RuntimeException("Message hasn't been handled correctly");
        }
    }

    @Then("^it is forwarded to the receiver$")
    public void itIsForwardedToTheReceiver() {
        Mockito.verify(receiverMock, Mockito.timeout(1000)).receive(Mockito.any());
        Mockito.verify(receiverMock, Mockito.times(1)).receive(Mockito.any());
    }
}