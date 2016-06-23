package feature.common;

import org.springframework.stereotype.Component;

/**
 * Created by Alexandru Obada on 23/06/16.
 */
@Component
public class TestEnvironment {
    public TestEnvironment() {
        System.out.println("Test environment");
    }
}
