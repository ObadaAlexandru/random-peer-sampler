package de.tum.communication.service.clients;

import de.tum.communication.protocol.messages.Message;
import de.tum.communication.service.Client;
import de.tum.communication.service.Module;
import de.tum.communication.service.Receiver;
import de.tum.communication.service.network.MessageDecoder;
import de.tum.communication.service.network.MessageEncoder;
import de.tum.communication.service.network.ReceiveMessageChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PreDestroy;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static de.tum.communication.service.Module.Service.BASE;

/**
 * Created by Nicolas Frinker on 19/05/16.
 */
@Slf4j
@Component
@Module(BASE)
public class ClientImpl implements Client {
    private final ReceiveMessageChannelHandler handler;
    private final EventLoopGroup workerGroup;
    private Map<SocketAddress, Channel> persistentConnections;

    public ClientImpl() {
        persistentConnections = new ConcurrentHashMap<>();
        handler = new ReceiveMessageChannelHandler();
        workerGroup = new NioEventLoopGroup();
    }

    private Optional<ChannelFuture> connect(SocketAddress address) {
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

            return Optional.of(b.connect(address).sync());
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
        throw new NotImplementedException();
    }

    @Override
    public void sendPersistent(Message data, SocketAddress address) {
        Channel channel = persistentConnections.get(address);
        if(channel == null || !channel.isOpen()) {
            Optional<ChannelFuture> channelFutureOptional = connect(address);
            if(channelFutureOptional.isPresent()) {
                ChannelFuture channelFuture = channelFutureOptional.get();
                channelFuture.addListener(new PersistentConnectionListener());
                persistentConnections.put(address, channelFuture.channel());
            }
            return;
        }
        channel.writeAndFlush(data);
    }

    @Override
    public Void send(Message data, SocketAddress address) {
        Optional<ChannelFuture> channelFutureOptional = connect(address);
        if(channelFutureOptional.isPresent()) {
            ChannelFuture channelFuture = channelFutureOptional.get();
            channelFuture.addListener(ChannelFutureListener.CLOSE);
            channelFuture.channel().writeAndFlush(data);
        }
        return null;
    }

    private class PersistentConnectionListener implements ChannelFutureListener {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if(future.isDone()) {
                if(future.isCancelled() || !future.isSuccess()) {
                    persistentConnections.remove(future.channel().remoteAddress());
                }
            }
        }
    }
}