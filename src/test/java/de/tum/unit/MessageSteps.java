package de.tum.unit;

import static com.google.common.truth.Truth.assertThat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

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

/**
 * Cucumber steps for {@link Message} implementations
 */
public class MessageSteps {

    private Message message;
    private List<Byte> messageBytes;

    @Given("^a gossip announce message with \"([^\"]*)\" and \"([^\"]*)\" and \"([^\"]*)\"$")
    public void aGossipAnnounceMessageWithTtlAndDatatypeAndPayload(Short ttl, Integer datatype, final String payload) {
		message = GossipAnnounceMessage.builder()
				.ttl(ttl)
				.datatype(datatype)
				.payload(new ByteSerializable() {
						@Override
						public List<Byte> getBytes() {
							return Bytes.asList(payload.getBytes());
						}
				})
				.build();
	}

    @Given("^a Gossip Notify Message$")
    public void aGossipNotifyMessage() {
        message = new GossipNotifyMessage();
    }

    @Given("^a gossip notification message with \"([^\"]*)\" and \"([^\"]*)\"$")
    public void aGossipNotificationMessageWithDatatypeAndPayload(Integer datatype, final String payload) {
		message = GossipNotificationMessage.builder()
				.datatype(datatype)
				.payload(new ByteSerializable() {
						@Override
						public List<Byte> getBytes() {
							return Bytes.asList(payload.getBytes());
						}
				})
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
        InetAddress address = InetAddress.getByName(ip);
        message = RpsPeerMessage.builder()
                .address(address)
                .identifier(identifier)
                .port(port)
                .build();
    }

    @When("^the message is serialized$")
    public void theMessageIsSerialized() {
        messageBytes = message.getBytes();
    }

    @Then("^the following byte sequence is returned: \"([^\"]*)\"$")
    public void theFollowingByteSequenceIsReturned(String byteSequence) {
        String expectedByteSequence = CharMatcher.WHITESPACE.removeFrom(byteSequence);
        String actualByteSequence = BaseEncoding.base16().encode(Bytes.toArray(messageBytes));
        assertThat(actualByteSequence.toUpperCase()).isEqualTo(expectedByteSequence.toUpperCase());
    }
}