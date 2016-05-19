package de.tum.communication.service.clients;

import org.springframework.stereotype.Service;

import de.tum.communication.service.Module;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Service
@Module(Module.Service.GOSSIP)
public class GossipClient extends GenericClient {

    public GossipClient(String host, int port) {
        super(host, port);
    }
}
