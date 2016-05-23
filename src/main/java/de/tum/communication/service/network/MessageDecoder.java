package de.tum.communication.service.network;

import com.google.common.primitives.Bytes;
import de.tum.communication.protocol.Protocol;
import de.tum.communication.protocol.ProtocolImpl;
import de.tum.communication.protocol.messages.Message;
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
        Protocol protocol = new ProtocolImpl();
        out.add(protocol.deserialize(Bytes.asList(data)));
    }
}