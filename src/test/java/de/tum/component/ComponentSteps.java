package de.tum.component;

import cucumber.api.PendingException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tum.config.AppConfig;
import de.tum.config.TestInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by Alexandru Obada on 09/05/16.
 */
@ContextConfiguration(classes = AppConfig.class)
public class ComponentSteps {

    @Autowired
    private TestInterface test;

    @When("^test$")
    public void test() {
        test.sayHello();
    }
}
