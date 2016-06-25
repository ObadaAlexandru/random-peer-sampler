package de.tum.sampling.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.google.common.primitives.Bytes;

import de.tum.sampling.entity.Peer;

/**
 * Created by Nicolas Frinker on 20/06/16.
 */

/**
 * Represents one sampling unit in brahms algorithm Should be fed with a stream
 * of incoming peers using next(), stores a single, randomly selected peer
 * in his cache and returns this with method sample().
 *
 * A call to init() reinitializes the sampling.
 */
public class SamplingUnit {
    private final MessageDigest md;
    private final Random prng = new Random();
    private byte[] rand = new byte[50];
    private Peer peer = null;

    public SamplingUnit() throws NoSuchAlgorithmException {
        this.md = MessageDigest.getInstance("SHA-256");
        this.init();
    }

    public Peer getPeer() {
        return this.peer;
    }

    /**
     * Initialize sample
     */
    public void init() {
        prng.nextBytes(this.rand);
    }

    /**
     * Update sample with input peer
     */
    public void next(Peer peer) {
        String newpeer = new String(md.digest(Bytes.concat(peer.getIdentifier().getBytes(), rand)));
        String oldpeer = (this.peer != null) ? new String(md.digest(Bytes.concat(this.peer.getIdentifier().getBytes(), rand))) : null;
        if (oldpeer == null || oldpeer.compareTo(newpeer) > 0)
            this.peer = peer;
    }

    /**
     * Retrieve currently stored sample
     *
     * @return stored peer
     */
    public Peer sample() {
        return peer;
    }
}
