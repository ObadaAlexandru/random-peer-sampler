package de.tum.unit;

import java.net.InetSocketAddress;

import de.tum.communication.protocol.messages.*;
import io.netty.channel.SimpleChannelInboundHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.mockito.Mockito;
import org.springframework.util.SocketUtils;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.tum.communication.service.Client;
import de.tum.communication.service.Receiver;
import de.tum.communication.service.clients.ClientImpl;
import de.tum.communication.service.network.MessageDecoder;
import de.tum.communication.service.network.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by Nicolas Frinker on 19/05/16.
 */
public class ClientSteps {
    private int serverport;

    private Client testclient;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private Receiver<Message> receiverMock;
    
    @Rule
    public final ErrorCollector collector = new ErrorCollector();
    
    private class TestChannelHandler extends SimpleChannelInboundHandler<Message> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
            switch (msg.getType()) {
            case NSE_QUERY:
                ctx.write(new NseEstimateMessage(15, 5));
                ctx.flush();
                break;
            case GOSSIP_NOTIFY:
                ctx.write(new GossipNotificationMessage(1, new NseQueryMessage()));
                ctx.flush();
                break;
            default:
                break;
            }
        }
    }
    private TestChannelHandler handler = new TestChannelHandler();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        receiverMock = (Receiver<Message>) Mockito.mock(Receiver.class);
    }
    
    private void startServer() {
        // Start test server
        serverport = SocketUtils.findAvailableTcpPort();
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(serverport))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new MessageEncoder());
                    ch.pipeline().addLast(new MessageDecoder());
                    ch.pipeline().addLast(handler);
                }
            });
            bootstrap.bind().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail("Failed to start test server!");
        }
    }

    @Given("^a nse client$")
    public void aNseClient() {
        startServer();
        testclient = new ClientImpl("localhost", serverport);
        testclient.setReceiver(receiverMock);
    }
    
    @Given("^a gossip client$")
    public void aGossipClient() {
        startServer();
        testclient = new ClientImpl("localhost", serverport);
        testclient.setReceiver(receiverMock);
    }
    
    @When("^a nse query is send$")
    public void aNseQueryIsSend() {
        testclient.send(new NseQueryMessage());
    }
    
    @When("^a gossip notify message is send$")
    public void aGossipNotifyMessageIsSend() {
        testclient.send(new GossipNotifyMessage());
    }

    @Then("^a nse estimation is received$")
    public void aValidNseEstimationIsReceived() {
        Mockito.verify(receiverMock, Mockito.timeout(1000)).receive(Mockito.any());
        Mockito.verify(receiverMock, Mockito.times(1)).receive(Mockito.any());
    }
    
    @Then("^some gossip notification is received$")
    public void someGossipNotificationsAreReceived() {
        Mockito.verify(receiverMock, Mockito.timeout(1000)).receive(Mockito.any());
        Mockito.verify(receiverMock, Mockito.times(1)).receive(Mockito.any());
    }
    
    @After
    public void tearDown() {
        try {
            if (bossGroup != null)
                bossGroup.shutdownGracefully();
            if (workerGroup != null)
                workerGroup.shutdownGracefully();
        } catch (Exception e) {
            Assert.fail("Failed to shutdown test server!");
        }
    }
}