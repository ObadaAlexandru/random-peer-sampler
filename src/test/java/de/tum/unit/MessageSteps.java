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
import de.tum.communication.messages.Message;
import de.tum.communication.messages.NseQueryMessage;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Cucumber steps for {@link Message} implementations
 */
public class MessageSteps {

    private Message message;
    private List<Byte> messageBytes;

    @Given("^a NSE Query Message$")
    public void aNSEQueryMessage() {
        message = new NseQueryMessage();
    }

    @When("^the message is serialized$")
    public void theMessageIsSerialized() {
        messageBytes = message.getBytes();
    }

    @Then("^the following byte sequence is returned: \"([^\"]*)\"$")
    public void theFollowingByteSequenceIsReturned(String byteSequence) {
        String expectedByteSequence = CharMatcher.WHITESPACE.removeFrom(byteSequence);
        String actualByteSequence = BaseEncoding.base16().encode(Bytes.toArray(messageBytes));
        assertThat(actualByteSequence).isEqualTo(expectedByteSequence);
    }
}
