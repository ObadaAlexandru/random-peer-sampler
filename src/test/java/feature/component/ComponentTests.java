package feature.component;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import feature.common.TestEnvironment;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Alexandru Obada on 09/05/16.
 */
@RunWith(Cucumber.class)
@CucumberOptions(strict = true, tags = "@component-test", features = "classpath:feature", plugin = {"pretty"},
        glue = {"feature.component"})
public class ComponentTests {
    @Configuration
    @ComponentScan("feature.common")
    public static class TestConfiguration {
    }
}
