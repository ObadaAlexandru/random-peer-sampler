package de.tum.sampling.entity;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.stereotype.Service;

/**
 * Created by Nicolas Frinker on 03/07/16.
 */
@Service
public class KeyValidatorImpl implements KeyValidator {
    public final static int EXPECTED_KEY_LENGTH = 4096;

    @Override
    public boolean isValidPublicKey(PublicKey pubkey) {

        if (pubkey instanceof RSAPublicKey && ((RSAPublicKey) pubkey).getModulus().bitLength() == EXPECTED_KEY_LENGTH) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isValidPrivateKey(PrivateKey privkey) {

        if (privkey instanceof RSAPrivateKey && ((RSAPrivateKey) privkey).getModulus().bitLength() == EXPECTED_KEY_LENGTH) {
            return true;
        }

        return false;
    }
}