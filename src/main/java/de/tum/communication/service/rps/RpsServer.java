package de.tum.communication.service.rps;

import de.tum.communication.service.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

/**
 * Created by Alexandru Obada on 12/05/16.
 */
@Service
@Value
@Slf4j
public class RpsServer implements Server, Runnable {

    //TODO this has to be injected via configuration
    private int port;

    private EventLoopGroup group;

    public RpsServer(int port) {
        this.port = port;
        group = new NioEventLoopGroup();
    }

    @Override
    public void start() throws Exception {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new LengthBasedInitializer());
            ChannelFuture f = bootstrap.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            this.shutdown();
        }
    }

    @Override
    public void shutdown() {
        try {
            group.shutdownGracefully().sync();
        } catch (Exception e) {
            //FIXME handle exception properly
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            this.start();
        } catch (Exception e) {
            //FIXME handle exception properly
            e.printStackTrace();
        }
    }
}
