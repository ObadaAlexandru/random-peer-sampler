package de.tum.config;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by Alexandru Obada on 31/05/16.
 */
public interface HostKeyReader {
    PublicKey getPublicKey();
    PrivateKey getPrivateKey();
}
