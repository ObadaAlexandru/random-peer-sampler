package de.tum.unit;

/**
 * Created by Alexandru Obada on 11/05/16.
 */

import com.google.common.base.CharMatcher;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tum.communication.protocol.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;

import static com.google.common.truth.Truth.assertThat;

/**
 * Cucumber steps for {@link Message} implementations
 */
public class MessageSteps {

    private Message message;
    private List<Byte> messageBytes;


    private Protocol protocol = new ProtocolImpl();
    private String byteSequence;

    @Given("^a gossip announce message with \"([^\"]*)\" and \"([^\"]*)\" and \"([^\"]*)\"$")
    public void aGossipAnnounceMessageWithTtlAndDatatypeAndPayload(Short ttl, Integer datatype, final String payload) {
        ByteSerializable payloadobj = Mockito.mock(ByteSerializable.class);
        Mockito.when(payloadobj.getBytes()).thenReturn(Bytes.asList(payload.getBytes()));
        message = GossipAnnounceMessage.builder()
				.ttl(ttl)
				.datatype(datatype)
				.payload(payloadobj)
				.build();
	}

    @Given("^a Gossip Notify Message$")
    public void aGossipNotifyMessage() {
        message = new GossipNotifyMessage();
    }

    @Given("^a gossip notification message with \"([^\"]*)\" and \"([^\"]*)\"$")
    public void aGossipNotificationMessageWithDatatypeAndPayload(Integer datatype, final String payload) {
        ByteSerializable payloadobj = Mockito.mock(ByteSerializable.class);
        Mockito.when(payloadobj.getBytes()).thenReturn(Bytes.asList(payload.getBytes()));
        message = GossipNotificationMessage.builder()
				.datatype(datatype)
				.payload(payloadobj)
				.build();
	}

    @Given("^an NSE Query Message$")
    public void aNSEQueryMessage() {
        message = new NseQueryMessage();
    }

    @Given("^an NSE estimation Message with estimated size \"([^\"]*)\" and standard deviation \"([^\"]*)\"$")
    public void anNSEEstimationMessageWithEstimatedSizeAndStandardDeviation(Integer size, Integer standardDeviation) {
        message = NseEstimateMessage.builder()
                .estimatedPeerNumbers(size)
                .estimatedStandardDeviation(standardDeviation).build();
    }

    @Given("^an RPS Query Message$")
    public void anRPSQueryMessage() {
        message = new RpsQueryMessage();
    }

    @Given("^an RPS message with \"([^\"]*)\" and \"([^\"]*)\" and \"([^\"]*)\"$")
    public void anRPSMessageWithAndAnd(String ip, String identifier, Short port) throws UnknownHostException {
        message = RpsPeerMessage.builder()
                .peer(CustomMocks.getPeer(ip, port, identifier)).build();
    }

    @Given("^an RPS view message with \"([^\"]*)\" and \"([^\"]*)\" and \"([^\"]*)\" and \"([^\"]*)\" and \"([^\"]*)\" and \"([^\"]*)\" and \"([^\"]*)\" and \"([^\"]*)\" and \"([^\"]*)\"$")
    public void anRPSViewMessageWithAndAndAndAndAndAndAndAnd(String address, String identifier, Short port, String address2, String identifier2, Short port2, String address3, String identifier3, Short port3) throws UnknownHostException {
        List<Peer> peerlist = new ArrayList<Peer>();
        peerlist.add(CustomMocks.getPeer(address2, port2, identifier2));
        peerlist.add(CustomMocks.getPeer(address3, port3, identifier3));
        message = RpsViewMessage.builder()
                .source(CustomMocks.getPeer(address, port, identifier))
                .peers(peerlist).build();
    }

    @When("^the message is serialized$")
    public void theMessageIsSerialized() {
        messageBytes = message.getBytes();
    }

    @Then("^the following byte sequence is returned: \"([^\"]*)\"$")
    public void theFollowingByteSequenceIsReturned(String byteSequence) {
        String expectedByteSequence = CharMatcher.WHITESPACE.removeFrom(byteSequence);
        String actualByteSequence = BaseEncoding.base16().encode(Bytes.toArray(messageBytes)).toUpperCase();
        assertThat(actualByteSequence).isEqualTo(expectedByteSequence.toUpperCase());
    }

    @Given("^the following byte sequence: \"([^\"]*)\"$")
    public void theFollowingByteSequence(String byteSequence) {
        this.byteSequence = byteSequence;
    }

    @When("^the message is deserialized$")
    public void theMessageIsDeserialized() {
        byte[] data = BaseEncoding.base16().decode(CharMatcher.WHITESPACE.removeFrom(byteSequence));
        message = protocol.deserialize(Bytes.asList(data));
    }

    @Then("^a message of type \"([^\"]*)\" is returned$")
    public void aMessageOfTypeIsReturned(MessageType messageType) {
        assertThat(message.getType()).isEqualTo(messageType);
    }
}