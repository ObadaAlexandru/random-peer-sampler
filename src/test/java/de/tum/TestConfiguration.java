package de.tum;

import de.tum.sampling.service.NseTestServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {
    @Bean
    public NseTestServer nseTestServer() {
        return new NseTestServer();
    }
}
