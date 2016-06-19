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
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.util.Optional;

/**
 * Created by Nicolas Frinker on 19/05/16.
 */
@Slf4j
public class ClientImpl implements Client {
    private final ReceiveMessageChannelHandler handler;
    private final EventLoopGroup workerGroup;

    public ClientImpl() {
        handler = new ReceiveMessageChannelHandler();
        workerGroup = new NioEventLoopGroup();
    }

    //    @Override
    public Optional<ChannelFuture> connect(@NonNull InetAddress host, @NonNull Integer port) {
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
            return Optional.of(b.connect(host, port).sync());
        } catch (Exception e) {
            log.error("Client connection failed!");
            log.error(e.getMessage());
        }
        return Optional.empty();
    }

    @PreDestroy
    public void shutdown() {
        workerGroup.shutdownGracefully();
    }

    @Override
    public void setReceiver(Receiver<Message> receiver) {
        handler.setReceiver(receiver);
    }

    @Override
    public Void send(Message data) {
//        if(chfuture.isDone()) {
//            chfuture = connect().get();
//        }
//        chfuture.channel().writeAndFlush(data);
//        log.info("Sent message of type {}", data.getType());
        return null;
    }

    @Override
    public Void send(Message data, InetAddress peerAddress, Integer port) {
        return null;
    }
}