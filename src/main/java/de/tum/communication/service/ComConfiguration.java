package de.tum.communication.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.tum.communication.service.clients.ClientImpl;
import de.tum.config.AppConfig;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by Alexandru Obada on 16/05/16.
 */
@Configuration
public class ComConfiguration {

    @Bean
    public ChannelHandler lengthFieldBasedDecoder() {
        final int MAX_PACKET_SIZE = 64000;
        final int LENGTH_FIELD_LENGTH = 2;
        return new LengthFieldBasedFrameDecoder(MAX_PACKET_SIZE, 0, LENGTH_FIELD_LENGTH, -2, 0);
    }

    @Bean
    @Module(Module.Service.GOSSIP)
    public Client getGossipClient(AppConfig config) {
        return new ClientImpl(config.getGossipHost(), config.getGossipPort());
    }

    @Bean
    @Module(Module.Service.NSE)
    public Client getNseClient(AppConfig config) {
        return new ClientImpl(config.getNseHost(), config.getNsePort());
    }
}
