package de.tum.sampling.service;

import static de.tum.sampling.entity.PeerType.SAMPLED;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;

import de.tum.sampling.entity.Peer;
import de.tum.sampling.repository.PeerRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Nicolas Frinker on 20/06/16.
 */

/**
 * Represents a sampling unit in Brahms algorithm. Should be fed with a stream
 * of incoming peers using next(), stores a single, randomly selected peer
 * in his cache and returns this with method sample().
 *
 * A call to init() reinitializes the sampling.
 */
@Slf4j
public class SamplingUnit {
    private final MessageDigest md;
    private final Random prng = new Random();
    private byte[] rand = new byte[50];
    private Peer peer = null;
    private PeerRepository peerRepository;

    public SamplingUnit(PeerRepository peerRepository) throws NoSuchAlgorithmException {
        this.peerRepository = peerRepository;
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
        this.peer = null;
        prng.nextBytes(this.rand);
    }

    /**
     * Update sample with input peer
     */
    public void next(Peer peer) {
        String newpeer = BaseEncoding.base16().encode(md.digest(Bytes.concat(peer.getIdentifier().getBytes(), rand)));
        String oldpeer = (this.peer != null)
                ? BaseEncoding.base16().encode(md.digest(Bytes.concat(this.peer.getIdentifier().getBytes(), rand)))
                : null;

        if (oldpeer == null || oldpeer.compareTo(newpeer) > 0) {
            log.debug("New peer in SamplingUnit: " + peer);
            if(this.peer != null) {
                peerRepository.deleteById(this.peer.getId());
            }
            this.peer = peer;
            peerRepository.save(
                    Peer.builder()
                    .address(peer.getAddress())
                    .hostkey(peer.getHostkey())
                    .port(peer.getPort())
                    .peerType(SAMPLED)
                    .build());
        }
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
