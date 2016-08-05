package de.tum.config;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import de.tum.common.exceptions.InvalidConfigurationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class IniConfig {
    private Ini ini;

    @Autowired
    public IniConfig(ApplicationArguments args, @Value("${rps.config.default_config_path}") String defaultConfigPath) {
        String path = defaultConfigPath;
        List<String> configpaths = args.getOptionValues("c");
        if (configpaths != null && configpaths.size() > 0) {
            path = configpaths.get(0);
            log.info("Loading config file: " + path);
        }
        this.load(path);
    }

    private void load(String path) {
        try {
            ini = new Wini(new File(path));
        } catch(InvalidFileFormatException e) {
            log.error("Malformed configuration file {}", path);
            throw new InvalidConfigurationException("Malformed configuration file");
        } catch (IOException e) {
            log.error("Failed loading configuration {}", path);
            throw new InvalidConfigurationException("Failed loading configuration");
        }
    }

    public String getBootstrapPath() {
        String bootstrapPath = ini.get("RPS", "bootstrap_file");
        if (null == bootstrapPath) {
            return "config/bootstrap.yaml";
        } else {
            return bootstrapPath;
        }
    }

    public InetAddress getRPSHost() {
        return getHost("RPS");
    }

    public Integer getRPSPort() {
        return Optional.ofNullable(getPort("RPS")).orElse(9999);
    }

    public InetAddress getGossipHost() {
        return getHost("GOSSIP");
    }

    public int getGossipPort() {
        return Optional.ofNullable(getPort("GOSSIP")).orElse(7000);
    }

    public InetAddress getNseHost() {
        return getHost("NSE");
    }

    public int getNsePort() {
        return Optional.ofNullable(getPort("NSE")).orElse(8000);
    }

    public String getHostKeyPath() {
        String hostkey = ini.get("?", "HOSTKEY");
        if(null == hostkey) {
            throw new InvalidConfigurationException("Hostkey path not specified");
        }
        return hostkey;
    }

    public Integer getRoundDuration() {
        return Optional.ofNullable(ini.get("RPS", "round_duration")).map(Integer::parseInt).orElse(5000);
    }

    public Integer getSamplerNum() {
        return Optional.ofNullable(ini.get("RPS", "sampler_num")).map(Integer::parseInt).orElse(100);
    }

    public Integer getValidationRate() {
        return Optional.ofNullable(ini.get("RPS", "validation_rate")).map(Integer::parseInt).orElse(300000);
    }

    public Integer getSamplerTimeout() {
        return Optional.ofNullable(ini.get("RPS", "sampler_timeout")).map(Integer::parseInt).orElse(30000);
    }

    public Double getPullRatio() {
        return Optional.ofNullable(ini.get("RPS", "pull_ratio")).map(Double::parseDouble).orElse(0.10);
    }

    private Integer getPort(String section) {
        try {
            ConfigAddress address = new ConfigAddress(ini.get(section, "api_address"));
            return address.getPort();
        } catch (URISyntaxException e) {
            log.error("{} api port is invalid!", section);
            return null;
        }
    }

    private InetAddress getHost(String section) {
        try {
            ConfigAddress address = new ConfigAddress(ini.get(section, "api_address"));
            return InetAddress.getByName(address.getHost());
        } catch (URISyntaxException e) {
            log.error("{} api address is invalid fallback to localhost!", section);
            try {
                return InetAddress.getLocalHost();
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
                throw new RuntimeException("Invalid address");
            }
        } catch (UnknownHostException e) {
            log.error("Invalid address in section {}", section);
            throw new RuntimeException("Invalid address");
        }
    }

    @Getter
    private class ConfigAddress {
        private final String host;
        private final Integer port;

        ConfigAddress(String address) throws URISyntaxException {
            URI uri = new URI("my://" + address);
            host = uri.getHost();
            port = uri.getPort();

            if (uri.getHost() == null || uri.getPort() == -1) {
                throw new URISyntaxException(uri.toString(), "URI must have host and port parts");
            }
        }
    }
}
