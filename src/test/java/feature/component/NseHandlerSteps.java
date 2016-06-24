package feature.component;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import feature.common.NseEstimationTest;

import java.util.List;

/**
 * Created by Alexandru Obada on 23/06/16.
 */
public class NseHandlerSteps {

    @Given("^that the NSE server is running on port (\\d+)$")
    public void thatTheNSEServerIsRunningOnPort(int nsePort) {
    }

    @And("^that it responds as follows:$")
    public void thatItRespondsAsFollows(List<NseEstimationTest> data) {
    }

    @When("^the handler is asked$")
    public void theHandlerIsAsked() {
    }

    @Then("^it responds as follows:$")
    public void itRespondsAsFollows(List<NseEstimationTest> data) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
