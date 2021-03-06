package de.tum.sampling.entity;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by Nicolas Frinker on 03/07/16.
 *
 *  Responsible for Public/Private key validation
 *  e.g. criteria key length
 */
public interface KeyValidator {

    /**
     * Checks, whether given public key is considered valid for this application
     *
     * @param pubkey
     * @return true if order is valid, false otherwise
     */
    boolean isValidPublicKey(PublicKey pubkey);

    /**
     * Checks, whether given private key is considered valid for this application
     *
     * @param privkey
     * @return true if order is valid, false otherwise
     */
    boolean isValidPrivateKey(PrivateKey privkey);
}
