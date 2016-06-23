package de.tum.config;

import com.google.common.truth.Truth;
import de.tum.common.exceptions.BootstrapException;
import de.tum.sampling.entity.Peer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FileBootstrapTest {

    @Test
    public void validExistingBootstrapFile() throws IOException {
        Bootstrap bootstrap = new FileBootstrap("src/test/resources/data/sample_bootstrap.yaml");

        List<Peer> peers = bootstrap.getPeers();

        Truth.assertThat(peers).hasSize(2);
        peers.forEach(peer -> {
            try {
                Truth.assertThat(peer.getAddress().equals(InetAddress.getByName("192.168.1.125")));
                Truth.assertThat(peer.getPort()).isEqualTo(9090);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    @Test(expected = BootstrapException.class)
    public void bootstrapFileNotFound() {
        Bootstrap bootstrap = new FileBootstrap("non/existing/path");

        bootstrap.getPeers();
    }

    @Test(expected = BootstrapException.class)
    public void invalidAddressInBootstrap() {
        Bootstrap bootstrap = new FileBootstrap("src/test/resources/data/bootstrap_with_invalid_address.yaml");

        bootstrap.getPeers();
    }

    @Test(expected = BootstrapException.class)
    public void malformedBootstrapFile() {
        Bootstrap bootstrap = new FileBootstrap("src/test/resources/data/bootstrap_with_syntax_errors.yaml");

        bootstrap.getPeers();
    }

    @Test(expected = BootstrapException.class)
    public void invalidPublicKeyInBootstrap() {
        Bootstrap bootstrap = new FileBootstrap("src/test/resources/data/bootstrap_with_invalid_key.yaml");

        bootstrap.getPeers();
    }
}