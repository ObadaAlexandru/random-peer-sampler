package de.tum.sampling.entity;

import java.net.InetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.tum.config.HostKeyReader;

@Component
public class SourcePeer extends Peer {

    @Autowired
    public SourcePeer(HostKeyReader hostKeyReader,
            @Value("#{iniConfig.getRPSHost()}") InetAddress rpsHost,
            @Value("#{iniConfig.getRPSPort()}") Integer rpsPort) {
        super();
        this.setPort(rpsPort);
        this.setAddress(rpsHost);
        this.setHostkey(hostKeyReader.getPublicKey());
    }
}
