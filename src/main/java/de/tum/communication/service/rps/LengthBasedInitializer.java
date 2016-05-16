package de.tum.communication.service.rps;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by Alexandru Obada on 15/05/16.
 */
public class LengthBasedInitializer extends ChannelInitializer<Channel> {
    private final int MAX_PACKET_SIZE = 64000;
    private final int LENGTH_FIELD_LENGTH = 2;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(MAX_PACKET_SIZE, 0, LENGTH_FIELD_LENGTH, -2, 0))
                .addLast(new MessageCodec())
                .addLast(new RpsChannelHandler());
    }
}
