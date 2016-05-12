package de.tum.communication.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Service
public class RpsServer implements Server {

    @Value("communication.port?8080")
    private int port;

    @Override
    public void start() {

    }
}
