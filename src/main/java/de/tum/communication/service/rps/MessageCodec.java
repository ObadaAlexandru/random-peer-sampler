package de.tum.communication.service.rps;

import com.google.common.primitives.Bytes;
import de.tum.communication.protocol.Message;
import de.tum.communication.protocol.Protocol;
import de.tum.communication.protocol.ProtocolImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Alexandru Obada on 14/05/16.
 */
@Component
public class MessageCodec extends ByteToMessageCodec<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        List<Byte> bytes = msg.getBytes();
        out.writeBytes(Bytes.toArray(bytes));
        ByteBuf buff = Unpooled.buffer(bytes.size());
        buff.writeBytes(Bytes.toArray(bytes));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] data = new byte[in.readableBytes()];
        in.readBytes(data);
        Protocol protocol = new ProtocolImpl();
        out.add(protocol.deserialize(Bytes.asList(data)));
    }
}
