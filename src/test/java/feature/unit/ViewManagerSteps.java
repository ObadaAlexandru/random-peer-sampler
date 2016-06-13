package feature.unit;


import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tum.sampling.entity.Peer;
import de.tum.sampling.entity.PeerType;
import de.tum.sampling.repository.PeerRepository;
import de.tum.sampling.service.Sampler;
import de.tum.sampling.service.ViewManager;
import de.tum.sampling.service.ViewManagerImpl;
import feature.common.TestPeer;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

import static de.tum.sampling.entity.PeerType.*;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Alexandru Obada on 13/06/16.
 */
public class ViewManagerSteps {
    List<Peer> pushedPeers;
    List<Peer> pulledPeers;
    List<Peer> sampledPeers;

    double alpha = 0.4;
    double beta = 0.4;
    double gamma = 0.2;
    int dynamicViewSize = 10;

    @Mock
    PublicKey publicKey;

    @Mock
    Sampler sampler;

    @Mock
    PeerRepository peerRepository;

    ViewManager viewManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        viewManager = ViewManagerImpl.builder()
                .dynamicViewSize(dynamicViewSize)
                .peerRepository(peerRepository)
                .alpha(alpha)
                .beta(beta)
                .gamma(gamma)
                .sampler(sampler)
                .build();
    }

    @Given("^the following pushed peers:$")
    public void theFollowingPushedPeers(List<TestPeer> pushedPeers) {
        this.pushedPeers = convertToPeers(pushedPeers, PUSHED);
        when(peerRepository.deleteByPeerType(PUSHED)).thenReturn(this.pushedPeers);
    }

    @And("^the following pulled peers:$")
    public void theFollowingPulledPeers(List<TestPeer> pulledPeers) {
        this.pulledPeers = convertToPeers(pulledPeers, PULLED);
        when(peerRepository.deleteByPeerType(PULLED)).thenReturn(this.pushedPeers);
    }

    @And("^the following sampled peers:$")
    public void theFollowingSampledPeers(List<TestPeer> sampledPeers) {
        this.sampledPeers = convertToPeers(sampledPeers, SAMPLED);
        when(peerRepository.getByPeerType(SAMPLED)).thenReturn(this.sampledPeers);
    }

    @When("^the view is updated$")
    public void theViewIsUpdated() {
        viewManager.updateView();
    }

    @Then("^the new dynamic view is persisted$")
    public void theNewDynamicViewIsPersisted() {
        verify(peerRepository).deleteByPeerType(PUSHED);
        verify(peerRepository).deleteByPeerType(PULLED);
        verify(peerRepository).getByPeerType(SAMPLED);
        verify(peerRepository).deleteByPeerType(DYNAMIC);
        verify(peerRepository).save(anyListOf(Peer.class));
    }

    @And("^the samples are updated$")
    public void theSamplesAreUpdated() {
        verify(sampler).updateSample(anyListOf(Peer.class));
    }

    @Then("^the dynamic view is not updated$")
    public void theDynamicViewIsNotUpdated() {
        verify(peerRepository, times(0)).getByPeerType(SAMPLED);
        verify(peerRepository, times(0)).deleteByPeerType(DYNAMIC);
        verify(peerRepository, times(0)).save(anyList());
        verify(sampler).updateSample(anyListOf(Peer.class));
    }

    private List<Peer> convertToPeers(List<TestPeer> testPeers, PeerType peerType) {
        return testPeers.stream().map(testPeer -> {
            try {
                return Peer.builder()
                        .port(testPeer.getPort())
                        .address(InetAddress.getByName(testPeer.getAddress()))
                        .hostkey(publicKey)
                        .peerType(peerType)
                        .build();
            } catch (UnknownHostException e) {
                throw new RuntimeException();
            }
        }).collect(Collectors.toList());
    }
}
