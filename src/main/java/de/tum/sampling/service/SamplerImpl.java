package de.tum.sampling.service;

import de.tum.sampling.entity.Peer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Alexandru Obada on 13/06/16.
 */
@Service
public class SamplerImpl implements Sampler {
    @Override
    public void updateSample(List<Peer> peers) {
        //TODO in progress
    }
}
