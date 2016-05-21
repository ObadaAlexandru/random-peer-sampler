package de.tum.component;

import cucumber.api.java.en.When;
import de.tum.Application;
import de.tum.config.AppConfig;
import de.tum.sampling.entity.Peer;
import de.tum.sampling.repository.PeerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by Alexandru Obada on 09/05/16.
 */
@SpringApplicationConfiguration(Application.class)
public class ComponentSteps {

    @Autowired
    private PeerRepository repository;

    @When("^test$")
    public void test() {
        for(int i=0; i<100; i++) {
            Peer peer = new Peer();
            peer.setAddress("123.123.123");
            peer.setPort((short)8080);
            peer.setIdentifier(i + "");
            repository.save(peer);
            System.out.println(peer);
        }
//        repository.findAll().forEach(System.out::println);
    }
}
