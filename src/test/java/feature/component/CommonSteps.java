package feature.component;

import cucumber.api.java.Before;
import de.tum.Application;
import de.tum.sampling.repository.PeerRepository;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
@SpringApplicationConfiguration(Application.class)
public class CommonSteps {


    @Autowired
    private PeerRepository repository;

    @Before
    public void setUp() throws IOException {
        /**
         * Clean db before test
         */
        repository.findAll().forEach(peer -> repository.delete(peer));
    }
}
