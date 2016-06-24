package feature.component;

import cucumber.api.java.Before;
import de.tum.Application;
import de.tum.sampling.repository.PeerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;

import java.io.IOException;

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
