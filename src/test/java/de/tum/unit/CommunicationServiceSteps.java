package de.tum.unit;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tum.communication.protocol.Message;
import de.tum.communication.protocol.MessageType;
import de.tum.communication.service.*;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
public class CommunicationServiceSteps {

    @InjectMocks
    private CommunicationService communicationService;

    private Client gossipMock;
    private Client nseMock;

    private Server rpsServerMock;
    private MessageType messageType;
    private Receiver<Message> receiverMock;
    private Message messageMock;

    @Before
    public void setUp() {
        gossipMock = Mockito.mock(Client.class);
        nseMock = Mockito.mock(Client.class);
        rpsServerMock = Mockito.mock(Server.class);
        communicationService = new CommunicationServiceImpl(gossipMock, nseMock, rpsServerMock);
        MockitoAnnotations.initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Given("^that the communication service has a receiver of \"([^\"]*)\"$")
    public void thatTheCommunicationServiceHasAReceiverOf(MessageType receivedMessageType) {
        receiverMock = (Receiver<Message>) Mockito.mock(Receiver.class);
        messageMock  = getMessage(receivedMessageType);
        Mockito.when(receiverMock.receive(Mockito.any())).thenReturn(Optional.empty());
        communicationService.addReceiver(receiverMock, receivedMessageType);
    }

    @Given("^a message of type \"([^\"]*)\" has to be sent$")
    public void aMessageOfTypeHasToBeSent(MessageType messageType) {
        this.messageType = messageType;
    }

    @When("^the message is sent$")
    public void theMessageIsSent() {
        messageMock = getMessage(messageType);
        communicationService.send(messageMock);
    }

    @When("^the message is received$")
    public void theMessageIsReceived() {
        communicationService.receive(messageMock);
    }

    @Then("^it is forwarded to the client \"([^\"]*)\"$")
    public void itIsForwardedToTheClient(Module.Service serviceType) {
        if(Module.Service.NSE.equals(serviceType)) {
            Mockito.verify(nseMock, Mockito.times(1)).send(messageMock);
        } else if(Module.Service.GOSSIP.equals(serviceType)) {
            Mockito.verify(gossipMock, Mockito.times(1)).send(messageMock);
        } else {
            throw new RuntimeException("Message hasn't been handled correctly");
        }
    }

    @Then("^it is forwarded to the receiver$")
    public void itIsForwardedToTheReceiver() {
        Mockito.verify(receiverMock, Mockito.times(1)).receive(Mockito.any());
    }

    private Message getMessage(MessageType messageType) {
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getType()).thenReturn(messageType);
        Mockito.when(message.getBytes()).thenReturn(new ArrayList<>());
        return message;
    }

}