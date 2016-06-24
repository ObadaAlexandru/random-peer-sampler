package de.tum;

import de.tum.sampling.service.NseTestServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application-component.yml")
public class TestConfiguration {
    @Bean
    public NseTestServer nseTestServer() {
        return new NseTestServer();
    }
}
