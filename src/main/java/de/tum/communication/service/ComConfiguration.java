package de.tum.communication.service;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Alexandru Obada on 16/05/16.
 */

/**
 * Configuration values for communication purposes
 */
@Configuration
public class ComConfiguration {

    @Bean
    public ChannelHandler lengthFieldBasedDecoder() {
        final int MAX_PACKET_SIZE = 64000;
        final int LENGTH_FIELD_LENGTH = 2;
        return new LengthFieldBasedFrameDecoder(MAX_PACKET_SIZE, 0, LENGTH_FIELD_LENGTH, -2, 0);
    }
}
