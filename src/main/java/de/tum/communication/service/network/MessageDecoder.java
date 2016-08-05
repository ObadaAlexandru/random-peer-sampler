package de.tum.communication.service.network;

import java.util.List;

import com.google.common.primitives.Bytes;

import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.Protocol;
import de.tum.communication.protocol.ProtocolImpl;
import de.tum.communication.protocol.messages.GossipNotificationMessage;
import de.tum.communication.protocol.messages.Message;
import de.tum.sampling.entity.ValidatorImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Created by Alexandru Obada on 16/05/16.
 */

/**
 * Decodes a sequence of bytes to a {@link Message} instance
 */
public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);
        Protocol protocol = new ProtocolImpl(new ValidatorImpl());

        Message message = protocol.deserialize(Bytes.asList(data));

        // Deserialize embedded messages from gossip notifications
        if (message.getType().equals(MessageType.GOSSIP_NOTIFICATION)) {
            GossipNotificationMessage notificationmsg = (GossipNotificationMessage) message;
            message = protocol.deserialize(notificationmsg.getPayload().getBytes());
        }

        out.add(message);
    }
}
