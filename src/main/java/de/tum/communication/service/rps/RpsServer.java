package de.tum.communication.service.rps;

import de.tum.communication.protocol.Message;
import de.tum.communication.service.Receiver;
import de.tum.communication.service.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Slf4j
@Service
@NoArgsConstructor
public class RpsServer implements Server {

    @Value("${rps.server.port:8080}")
    private int port;

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();


    private final RpsChannelInitializer channelInitializer = new RpsChannelInitializer();

    public RpsServer(int port) {
        this.port = port;
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
    }

    @Override
    public void setReceiver(Receiver<Message> receiver) {
        channelInitializer.setReceiver(receiver);
    }

    @Override
    public void start() throws Exception {
        log.info("RPS server starting on port {}", port);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(channelInitializer);
            ChannelFuture channelFuture = bootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            this.shutdown();
        }
    }

    @Override
    public void shutdown() {
        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
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