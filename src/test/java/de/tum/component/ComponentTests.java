package de.tum.component;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import de.tum.Application;
import de.tum.config.AppConfig;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by Alexandru Obada on 09/05/16.
 */
@RunWith(Cucumber.class)
@CucumberOptions(strict = true, tags = "@component-test", features = "classpath:features", plugin = {"pretty"},
        glue = {"de.tum.component"})
public class ComponentTests {
}
