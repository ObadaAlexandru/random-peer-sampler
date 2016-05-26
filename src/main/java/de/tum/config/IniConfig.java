package de.tum.config;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IniConfig {
    private final static String DEFAULT_CONFIG_PATH = "config.ini";
    private Ini ini;

    @Autowired
    public IniConfig(ApplicationArguments args) throws InvalidFileFormatException, IOException {
        String path = DEFAULT_CONFIG_PATH;
        List<String> configpaths = args.getOptionValues("c");
        if (configpaths != null && configpaths.size() > 0) {
            path = configpaths.get(0);
        }
        this.load(path);
    }

    private void load(String path) throws InvalidFileFormatException, IOException {
        ini = new Wini(new File(path));
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
            ConfigAddress address = new ConfigAddress(ini.get("NSE", "api_address"));
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
