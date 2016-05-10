package de.tum.config;

import org.springframework.stereotype.Component;

/**
 * Created by Alexandru Obada on 09/05/16.
 */
@Component
public class TestBean  implements TestInterface {
    @Override
    public void sayHello() {
        System.out.println("Hello bean");
    }
}
