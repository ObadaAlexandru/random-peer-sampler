package de.tum.communication.service.network;

import com.google.common.primitives.Bytes;
import de.tum.communication.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

/**
 * Created by Alexandru Obada on 16/05/16.
 */

/**
 *  Encodes {@link Message} to a sequence of bytes
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        List<Byte> bytes = msg.getBytes();
        out.writeBytes(Bytes.toArray(bytes));
        ByteBuf buff = Unpooled.buffer(bytes.size());
        buff.writeBytes(Bytes.toArray(bytes));
    }
}