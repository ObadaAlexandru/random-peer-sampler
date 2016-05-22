package de.tum.communication.service.clients;

import de.tum.communication.protocol.messages.Message;
import de.tum.communication.service.Client;
import de.tum.communication.service.Receiver;
import de.tum.communication.service.network.MessageDecoder;
import de.tum.communication.service.network.MessageEncoder;
import de.tum.communication.service.network.ReceiveMessageChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by Nicolas Frinker on 19/05/16.
 */
@Service
@Slf4j
public class ClientImpl implements Client {
    private final String host;
    private final int port;
    private ReceiveMessageChannelHandler handler = new ReceiveMessageChannelHandler();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private ChannelFuture chfuture;

    public ClientImpl(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new MessageEncoder());
                    ch.pipeline().addLast(new MessageDecoder());
                    ch.pipeline().addLast(handler);
                }
            });

            // Start the client.
            chfuture = b.connect(this.host, this.port).sync();
        } catch (Exception e) {
            log.error("Client connection failed!");
            log.error(e.getMessage());
        }
    }

    public void shutdown() {
        chfuture.channel().close();

        // Wait until the connection is closed.
        try {
            chfuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Client shutdown failed!");
            log.error(e.getMessage());
        }
        workerGroup.shutdownGracefully();
    }

    @Override
    public void setReceiver(Receiver<Message> receiver) {
        handler.setReceiver(receiver);
    }

    @Override
    public Void send(Message data) {
        chfuture.channel().writeAndFlush(data);
        return null;
    }
}
