package de.tum.config;

import com.google.common.net.InetAddresses;
import de.tum.common.exceptions.BootstrapException;
import de.tum.common.exceptions.HostkeyException;
import de.tum.sampling.entity.HostkeyConverter;
import de.tum.sampling.entity.Peer;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

import static de.tum.sampling.entity.PeerType.DYNAMIC;

/**
 *  File based bootstrap
 *  The peers are read from a yaml file
 */
@Slf4j
@Component
public class FileBootstrap implements Bootstrap {
    private String bootstrapPath;

    @Autowired
    public FileBootstrap(@Value("#{iniConfig.getBootstrapPath()}") String bootstrapPath) {
        this.bootstrapPath = bootstrapPath;
    }

    @Override
    public List<Peer> getPeers() {
        Constructor constructor = new Constructor(BootstrapPeers.class);
        TypeDescription typeDescription = new TypeDescription(BootstrapPeer.class, "peers");
        typeDescription.putMapPropertyType("peers", BootstrapPeer.class, Object.class);
        Yaml yaml = new Yaml(constructor);
        constructor.addTypeDescription(typeDescription);
        try {
            BootstrapPeers peers = (BootstrapPeers) yaml.load(new FileInputStream(new File(bootstrapPath)));
            return peers.peers.stream().map(BootstrapPeer::toPeer).collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            log.error("Bootstrap file not found at {}", bootstrapPath);
            throw new BootstrapException("Bootstrap file not found under the specified path");
        } catch (YAMLException e) {
            log.error("Malformed bootstrap file {}", bootstrapPath);
            throw new BootstrapException("Syntax errors in bootstrap file");
        }
    }

    @NoArgsConstructor
    private static class BootstrapPeer {
        public String address;
        public Integer port;
        public String key;

        Peer toPeer() {
            HostkeyConverter hostkeyConverter = new HostkeyConverter();
            try {
                if(!InetAddresses.isInetAddress(address)) {
                    log.error("Invalid address in bootstrap: {}", address);
                    throw new BootstrapException("Invalid address in bootstrap");
                }
                return Peer.builder()
                        .hostkey(hostkeyConverter.convertToEntityAttribute(key.replaceAll("(\\r|\\n|\\t)", "")))
                        .port(port)
                        .address(InetAddress.getByName(address))
                        .peerType(DYNAMIC)
                        .build();
            } catch (UnknownHostException e) {
                log.error("Invalid address in bootstrap: {}", address);
                throw new BootstrapException("Invalid address in bootstrap");
            } catch (HostkeyException e) {
                log.error("Bootstrap contains invalid key");
                throw new BootstrapException("Invalid key in bootstrap");
            }
        }
    }

    @NoArgsConstructor
    private static class BootstrapPeers {
        public List<BootstrapPeer> peers;
    }
}