package de.tum.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("de.tum.*")
public class AppConfig {
    //TODO some config

    public String getGossipHost() {
        // TODO derive from config file
        return "localhost";
    }

    public int getGossipPort() {
        // TODO derive from config file
        return 7000;
    }

    public String getNseHost() {
        // TODO derive from config file
        return "localhost";
    }

    public int getNsePort() {
        // TODO derive from config file
        return 7001;
    }
}
