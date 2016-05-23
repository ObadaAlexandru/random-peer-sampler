package de.tum.component;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tum.sampling.entity.Peer;
import de.tum.sampling.repository.PeerRepository;
import de.tum.sampling.service.ViewManager;
import de.tum.sampling.service.ViewManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;


/**
 * Created by Alexandru Obada on 22/05/16.
 */
public class ViewManagerSteps {

    @Autowired
    private ViewManager viewManager;

    @Autowired
    private PeerRepository peerRepository;

    private Set<Peer> incomingPeers;
    private Set<Peer> currentView;

    @Given("^the following incoming view:$")
    public void theFollowingIncomingView(List<TestPeer> testPeers) {
        incomingPeers = testPeerToPeer(testPeers);
    }

    private Set<Peer> testPeerToPeer(List<TestPeer> testPeers) {
        return testPeers.stream().map(testPeer -> {
            try {
                return Peer.builder()
                        .identifier(testPeer.getIdentifier())
                        .address(InetAddress.getByName(testPeer.getAddress()))
                        .port(testPeer.getPort())
                        .age(testPeer.getAge())
                        .build();
            } catch (UnknownHostException e) {
                throw new RuntimeException("Unknown host");
            }
        }).collect(Collectors.toSet());
    }

    @And("^the current view is as follows:$")
    public void theCurrentViewIsAsFollows(List<TestPeer> testPeers) {
        currentView = testPeerToPeer(testPeers);
        peerRepository.save(currentView);
    }

    @When("^the incoming view is merged$")
    public void theIncomingViewIsMerged() {
        viewManager.merge(incomingPeers);
    }

    @Then("^the resulting view is as follows:$")
    public void theResultingViewIsAsFollows(List<TestPeer> testPeers) {
        Set<Peer> expectedView = testPeerToPeer(testPeers);
        Map<String, Peer> expectedViewMap = expectedView.stream().collect(Collectors.toMap(Peer::getIdentifier, Function.identity()));
        Set<Peer> actualView = viewManager.getPeers();
        assertThat(actualView).hasSize(expectedView.size());
        actualView.forEach(peer -> {
            assertThat(expectedView.contains(peer));
            Peer expectedPeer = expectedViewMap.get(peer.getIdentifier());
            assertThat(peer.getAge()).isEqualTo(expectedPeer.getAge());
            assertThat(peer.getAddress()).isEqualTo(expectedPeer.getAddress());
            assertThat(peer.getPort()).isEqualTo(expectedPeer.getPort());
        });
    }

    @And("^the maximum view size is \"([^\"]*)\"$")
    public void theMaximumViewSizeIs(Integer maxViewSize) {
        ((ViewManagerImpl) viewManager).setViewSize(maxViewSize);
    }
}
