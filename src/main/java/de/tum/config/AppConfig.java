package de.tum.config;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.tum.Application;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@ComponentScan("de.tum.*")
public class AppConfig {
    private Ini ini;

    public AppConfig() throws InvalidFileFormatException, IOException {
        this.load(Application.commandline.getOptionValue("c", "config.ini"));
    }

    private void load(String path) throws InvalidFileFormatException, IOException {
        ini = new Wini(new File(path));
    }

    @Bean
    public AppConfig getConfig() {
        return this;
    }

    public String getGossipHost() {
        try {
            ConfigAddress address = new ConfigAddress(ini.get("GOSSIP", "api_address"));
            return address.getHost();
        } catch (URISyntaxException e) {
            log.error("Gossip api address is invalid!");
            return "localhost";
        }
    }

    public int getGossipPort() {
        try {
            ConfigAddress address = new ConfigAddress(ini.get("GOSSIP", "api_address"));
            return address.getPort();
        } catch (URISyntaxException e) {
            log.error("Gossip api port is invalid!");
            return 7000;
        }
    }

    public String getNseHost() {
        try {
            ConfigAddress address = new ConfigAddress(ini.get("NSE", "api_address"));
            return address.getHost();
        } catch (URISyntaxException e) {
            log.error("NSE api address is invalid!");
            return "localhost";
        }
    }

    public int getNsePort() {
        try {
            ConfigAddress address = new ConfigAddress(ini.get("GOSSIP", "api_address"));
            return address.getPort();
        } catch (URISyntaxException e) {
            log.error("Nse api port is invalid!");
            return 8000;
        }
    }

    @Getter
    private class ConfigAddress {
        private final String host;
        private final int port;

        public ConfigAddress(String address) throws URISyntaxException {
            URI uri = new URI("my://" + address);
            host = uri.getHost();
            port = uri.getPort();

            if (uri.getHost() == null || uri.getPort() == -1) {
                throw new URISyntaxException(uri.toString(), "URI must have host and port parts");
            }
        }
    }
}
