package feature.unit;

import static com.google.common.truth.Truth.assertThat;

import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

import org.mockito.Mockito;

import com.google.common.base.CharMatcher;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tum.communication.protocol.ByteSerializable;
import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.Protocol;
import de.tum.communication.protocol.ProtocolImpl;
import de.tum.communication.protocol.SerializablePeer;
import de.tum.communication.protocol.messages.GossipAnnounceMessage;
import de.tum.communication.protocol.messages.GossipNotificationMessage;
import de.tum.communication.protocol.messages.GossipNotifyMessage;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.NseEstimateMessage;
import de.tum.communication.protocol.messages.NseQueryMessage;
import de.tum.communication.protocol.messages.RpsPeerMessage;
import de.tum.communication.protocol.messages.RpsQueryMessage;
import de.tum.communication.protocol.messages.RpsViewMessage;
import feature.common.TestPeer;

/**
 * Created by Alexandru Obada on 11/05/16.
 */

/**
 * Cucumber steps for {@link Message} implementations
 */
public class MessageSteps {

    private Message message;
    private List<Byte> messageBytes;

    private Protocol protocol = new ProtocolImpl();
    private String byteSequence;

    @Given("^a gossip announce message with \"([^\"]*)\" and \"([^\"]*)\" and \"([^\"]*)\"$")
    public void aGossipAnnounceMessageWithTtlAndDatatypeAndPayload(Short ttl, Short datatype, final String payload) {
        ByteSerializable payloadObj = Mockito.mock(ByteSerializable.class);
        Mockito.when(payloadObj.getBytes()).thenReturn(Bytes.asList(payload.getBytes()));
        message = GossipAnnounceMessage.builder()
				.ttl(ttl)
				.datatype(datatype)
				.payload(payloadObj)
				.build();
	}

    @Given("^a Gossip Notify Message$")
    public void aGossipNotifyMessage() {
        message = new GossipNotifyMessage();
    }

    @Given("^a gossip notification message with \"([^\"]*)\" and \"([^\"]*)\"$")
    public void aGossipNotificationMessageWithDatatypeAndPayload(Integer dataType, final String payload) {
        ByteSerializable payloadObj = Mockito.mock(ByteSerializable.class);
        Mockito.when(payloadObj.getBytes()).thenReturn(Bytes.asList(payload.getBytes()));
        message = GossipNotificationMessage.builder()
				.datatype(dataType)
				.payload(payloadObj)
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
    public void anRPSMessageWithAndAnd(String ip, String hostkey, Short port) throws UnknownHostException {
        message = RpsPeerMessage.builder()
                .peer(CustomMocks.getPeer(ip, port, hostkey)).build();
    }

    @Given("^an RPS view message with:$")
    public void anRPSViewMessageWith(List<TestPeer> testPeers) {
        List<SerializablePeer> speers = CustomMocks.testPeerToPeer(testPeers).stream()
                .map(SerializablePeer::new).collect(Collectors.toList());
        message = RpsViewMessage.builder()
                .source(speers.remove(0))
                .peers(speers).build();
    }

    @Given("^a serialized RPS view message as \"([^\"]*)\"$")
    public void aMessageOfTypeIsReturned(String bytes) {
        this.byteSequence = bytes;
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

    @Then("^an RPS view message is returned with:$")
    public void anRPSViewMessageIsReturnedWith(List<TestPeer> testPeers) {
        List<SerializablePeer> speers = CustomMocks.testPeerToPeer(testPeers).stream()
                .map(SerializablePeer::new).collect(Collectors.toList());
        assertThat(message instanceof RpsViewMessage);
        RpsViewMessage rpsviewmessage = (RpsViewMessage) message;
        assertThat(speers.size() == rpsviewmessage.getPeers().size() + 1);
        assertThat(speers.get(0).equals(rpsviewmessage.getSource()));
        assertThat(speers.subList(1, speers.size() - 1).equals(rpsviewmessage.getPeers()));
    }
}