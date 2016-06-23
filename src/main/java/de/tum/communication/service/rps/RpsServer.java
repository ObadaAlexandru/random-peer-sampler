package de.tum.communication.service.rps;

import de.tum.communication.protocol.messages.Message;
import de.tum.communication.service.Receiver;
import de.tum.communication.service.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Slf4j
@Service
public class RpsServer implements Server {

    private int port;
    private InetAddress address;

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();


    private final RpsChannelInitializer channelInitializer = new RpsChannelInitializer();

    @Autowired
    public RpsServer(@Value("#{iniConfig.getRPSPort()}") Integer port, @Value("#{iniConfig.getRPSHost()}") InetAddress address) {
        this.port = port;
        this.address = address;
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
    }

    @Override
    public void setReceiver(Receiver<Message> receiver) {
        channelInitializer.setReceiver(receiver);
    }

    @Override
    public void start() throws Exception {
        log.info("RPS server starting on {}:{}", address.getHostName(), port);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(address, port))
                    .childHandler(channelInitializer);
            ChannelFuture channelFuture = bootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            this.shutdown();
        }
    }

    @PreDestroy
    @Override
    public void shutdown() {
        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("RPS server shutting down");
        } catch (Exception e) {
            log.error("Rps server failed to shutdown gracefully");
            log.error(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            this.start();
        } catch (Exception e) {
            log.error("Rps server failed to start");
            log.error(e.getMessage());
        }
    }
}