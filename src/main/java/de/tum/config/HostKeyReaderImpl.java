package de.tum.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.tum.common.exceptions.HostkeyException;
import de.tum.sampling.entity.KeyValidator;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Alexandru Obada on 31/05/16.
 */

/**
 * Hostkey reader implementation for pem format
 */
@Component
@Slf4j
public class HostKeyReaderImpl implements HostKeyReader {

    private String hostKeyPath;
    private JcaPEMKeyConverter keyConverter;
    private PEMKeyPair pemKeyPair;

    @Autowired
    private KeyValidator validator;

    @Autowired
    public HostKeyReaderImpl(@NonNull @Value("#{iniConfig.getHostKeyPath()}") String hostKeyPath) {
        Security.addProvider(new BouncyCastleProvider());
        keyConverter = new JcaPEMKeyConverter().setProvider("BC");
        this.hostKeyPath = hostKeyPath;
    }

    @Override
    public PublicKey getPublicKey() {
        PublicKey pubkey = null;
        PEMKeyPair keyPair = getKeyPair();
        try {
            pubkey = keyConverter.getPublicKey(keyPair.getPublicKeyInfo());
            if (!validator.isValidPublicKey(pubkey)) {
                log.error("Public key is invalid!");
                throw new HostkeyException("Public key is invalid!");
            }
        } catch (PEMException e) {
            log.error("Malformed key");
            throw new RuntimeException("Malformed key");
        }
        return pubkey;
    }

    @Override
    public PrivateKey getPrivateKey() {
        PrivateKey privkey = null;
        PEMKeyPair keyPair = getKeyPair();
        try {
            privkey = keyConverter.getPrivateKey(keyPair.getPrivateKeyInfo());
            if (!validator.isValidPrivateKey(privkey)) {
                log.error("Private key is invalid!");
                throw new HostkeyException("Private key is invalid!");
            }
        } catch (PEMException e) {
            log.error("Malformed key");
            throw new HostkeyException();
        }
        return privkey;
    }

    private synchronized PEMKeyPair getKeyPair() {
        if (pemKeyPair == null) {
            PEMParser privatePem = null;
            try {
                Reader rsaPrivate = new FileReader(hostKeyPath);
                privatePem = new PEMParser(rsaPrivate);
                pemKeyPair = (PEMKeyPair) privatePem.readObject();

            } catch (FileNotFoundException e) {
                log.error("Host key not found in {}", hostKeyPath);
                throw new HostkeyException(String.format("Missing hostkey at %s", hostKeyPath));
            } catch (IOException e) {
                log.error("Failed parsing host key");
                throw new HostkeyException();
            } finally {
                try {
                    if (privatePem != null)
                        privatePem.close();
                } catch (IOException e) {
                    log.error("Failed to close file handle of host key!");
                    throw new HostkeyException();
                }
            }
        }

        return pemKeyPair;
    }
}
