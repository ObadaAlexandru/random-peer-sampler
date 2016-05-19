package de.tum.communication.service.clients;

import org.springframework.stereotype.Service;

import de.tum.communication.service.Module;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Service
@Module(Module.Service.NSE)
public class NseClient extends GenericClient {

    public NseClient(String host, int port) {
        super(host, port);
    }
}
