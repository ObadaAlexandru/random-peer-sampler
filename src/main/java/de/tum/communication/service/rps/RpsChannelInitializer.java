package de.tum.communication.service.rps;

import de.tum.communication.protocol.Message;
import de.tum.communication.service.Receiver;
import de.tum.communication.service.ReceiverAware;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Value;

/**
 * Created by Alexandru Obada on 15/05/16.
 */
@Value
class RpsChannelInitializer extends ChannelInitializer<Channel> implements ReceiverAware<Message> {
    private final int MAX_PACKET_SIZE = 64000;
    private final int LENGTH_FIELD_LENGTH = 2;

    private final RpsChannelHandler rpsChannelHandler = new RpsChannelHandler();

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(15, 15, 15))
                .addLast()
                .addLast(new MessageDecoder())
                .addLast(new MessageEncoder())
                .addLast(rpsChannelHandler);
    }

    @Override
    public void setReceiver(Receiver<Message> receiver) {
        rpsChannelHandler.setReceiver(receiver);
    }
}
