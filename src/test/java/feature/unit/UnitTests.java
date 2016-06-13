package feature.unit;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Created by Alexandru Obada on 09/05/16.
 */
@RunWith(Cucumber.class)
@CucumberOptions(strict = true, tags = "@unit-test", features = "classpath:feature", plugin = {"pretty"},
glue = {"feature.unit", "de.tum"})
public class UnitTests {
}