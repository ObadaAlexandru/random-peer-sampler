package de.tum.unit;

import com.google.common.base.CharMatcher;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;
import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tum.communication.protocol.Message;
import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.RpsPeerMessage;
import de.tum.communication.protocol.RpsQueryMessage;
import de.tum.communication.service.ComConfiguration;
import de.tum.communication.service.Receiver;
import de.tum.communication.service.rps.MessageDecoder;
import de.tum.communication.service.rps.MessageEncoder;
import de.tum.communication.service.rps.RpsChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by Alexandru Obada on 16/05/16.
 */
public class RpsServerSteps {

    private EmbeddedChannel embeddedChannel;

    private Receiver<Message> receiverMock;

    private ByteBuf buffer;

    private Message message;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        ComConfiguration conf = new ComConfiguration();
        RpsChannelHandler rpsChannelHandler = new RpsChannelHandler();
        receiverMock = (Receiver<Message>) Mockito.mock(Receiver.class);
        rpsChannelHandler.setReceiver(receiverMock);
        embeddedChannel = new EmbeddedChannel(conf.lengthFieldBasedDecoder(), new MessageDecoder(), new MessageEncoder(), rpsChannelHandler);
    }

    @Given("^that the following byte sequence \"([^\"]*)\"$")
    public void thatTheFollowingByteSequence(String byteSequence) {
        byte[] data = BaseEncoding.base16().decode(CharMatcher.WHITESPACE.removeFrom(byteSequence));
        buffer = Unpooled.buffer(data.length);
        buffer.writeBytes(data);
    }

    @When("^the data is received$")
    public void theDataIsReceived() {
        embeddedChannel.writeInbound(buffer);
    }

    @Then("^the receiver receives a message of type \"([^\"]*)\"$")
    public void theReceiverReceivesAMessageOfType(MessageType messageType) {
        ArgumentCaptor<Message> argument = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(receiverMock).receive(argument.capture());
        assertThat(argument.getValue().getType()).isEqualTo(messageType);
    }

    @Given("^an RPS peer message with address \"([^\"]*)\", port \"([^\"]*)\" and id \"([^\"]*)\"$")
    public void anRPSPeerMessageWithAddressPortAndId(String address, short port, String identifier) throws UnknownHostException {
        message = RpsPeerMessage.builder()
                .address(InetAddress.getByName(address))
                .port(port)
                .identifier(identifier).build();
        List<Byte> bytes = message.getBytes();
        byte[] data = Bytes.toArray(bytes);
        buffer = Unpooled.buffer(data.length);
        buffer.writeBytes(data);
    }

    @When("^the message is sent out$")
    public void theMessageIsSentOut() {
        embeddedChannel.writeOutbound(buffer);
    }

    @Then("^the peer receives the following byte sequence \"([^\"]*)\"$")
    public void thePeerReceivesTheFollowingByteSequence(String expectedByteSequence) {
        ByteBuf actualBuff = (ByteBuf) embeddedChannel.readOutbound();
        byte[] bytes = new byte[actualBuff.readableBytes()];
        actualBuff.readBytes(bytes);
        String actualByteSequence = BaseEncoding.base16().encode(bytes).toUpperCase();
        assertThat(actualByteSequence).isEqualTo(expectedByteSequence);
    }
}
