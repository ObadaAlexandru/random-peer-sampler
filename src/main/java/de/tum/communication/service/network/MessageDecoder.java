package de.tum.communication.service.network;

import com.google.common.primitives.Bytes;
import de.tum.communication.protocol.MessageType;
import de.tum.communication.protocol.Protocol;
import de.tum.communication.protocol.ProtocolImpl;
import de.tum.communication.protocol.messages.GossipNotificationMessage;
import de.tum.communication.protocol.messages.GossipValidationMessage;
import de.tum.communication.protocol.messages.Message;
import de.tum.sampling.entity.KeyValidatorImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

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
        Protocol protocol = new ProtocolImpl(new KeyValidatorImpl());

        Message message = protocol.deserialize(Bytes.asList(data));

        // Deserialize embedded messages from gossip notifications
        if (message.getType().equals(MessageType.GOSSIP_NOTIFICATION)) {
            GossipNotificationMessage notificationMsg = (GossipNotificationMessage) message;
            try {
                message = protocol.deserialize(notificationMsg.getPayload().getBytes());
                ctx.write(getValidationMessageBytes(notificationMsg.getMessageId(), true));
            } catch (IllegalArgumentException e) {
                ctx.write(getValidationMessageBytes(notificationMsg.getMessageId(), false));
            }
        }
        out.add(message);
    }

    private List<Byte> getValidationMessageBytes(Short messageId, boolean valid) {
        return GossipValidationMessage.builder().messageId(messageId).valid(valid).build().getBytes();
    }
}
