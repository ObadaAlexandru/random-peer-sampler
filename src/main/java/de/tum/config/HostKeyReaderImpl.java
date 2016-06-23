package de.tum.config;

import de.tum.common.exceptions.HostkeyException;
import de.tum.common.exceptions.InvalidConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

/**
 * Created by Alexandru Obada on 31/05/16.
 */

/**
 * Parses the PEM hostkey file and extracts Public/Private key
 */
@Component
@Slf4j
public class HostKeyReaderImpl implements HostKeyReader {

    private String hostKeyPath;
    private JcaPEMKeyConverter keyConverter;
    private PEMKeyPair pemKeyPair;

    @Autowired
    public HostKeyReaderImpl(@Value("#{iniConfig.getHostKeyPath()}") String hostKeyPath) {
        if(hostKeyPath == null) {
            log.error("Host key path not specified");
            throw new InvalidConfigurationException("Host key path not specified");
        }
        Security.addProvider(new BouncyCastleProvider());
        keyConverter = new JcaPEMKeyConverter().setProvider("BC");
        this.hostKeyPath = hostKeyPath;
    }

    @Override
    public PublicKey getPublicKey() {
        PEMKeyPair keyPair = getKeyPair();
        try {
            return keyConverter.getPublicKey(keyPair.getPublicKeyInfo());
        } catch (PEMException e) {
            log.error("Malformed key");
            throw new RuntimeException("Malformed key");
        }
    }

    @Override
    public PrivateKey getPrivateKey() {
        PEMKeyPair keyPair = getKeyPair();
        try {
            return keyConverter.getPrivateKey(keyPair.getPrivateKeyInfo());
        } catch (PEMException e) {
            log.error("Malformed key");
            throw new HostkeyException();
        }
    }

    private synchronized PEMKeyPair getKeyPair() {
        if (pemKeyPair == null) {
            try {
                Reader rsaPrivate = new FileReader(hostKeyPath);
                PEMParser privatePem = new PEMParser(rsaPrivate);
                pemKeyPair = (PEMKeyPair) privatePem.readObject();
            } catch (FileNotFoundException e) {
                log.error("Host key not found in {}", hostKeyPath);
                throw new HostkeyException(String.format("Missing hostkey at %s", hostKeyPath));
            } catch (IOException e) {
                log.error("Failed parsing host key");
                throw new HostkeyException();
            }
        }
        return pemKeyPair;
    }
}