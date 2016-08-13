package de.tum.communication.service.network;

import de.tum.communication.protocol.messages.Message;
import de.tum.communication.service.Receiver;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Alexandru Obada on 16/05/16.
 */

/**
 * Netty inbound handler for receiving incoming messages and dispatching them to
 * the right receivers and optinally sending out responses
 */
@Slf4j
@NoArgsConstructor
@ChannelHandler.Sharable
public class ReceiveMessageChannelHandler extends SimpleChannelInboundHandler<Message> {

    @Setter
    private Receiver<Message> receiver;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        log.info("Handle message {}", msg.getType());
        receiver.receive(msg).ifPresent(ctx::writeAndFlush);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage());
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.ALL_IDLE) {
                log.warn("Peer {} holds the channel idle, the connections is going to be closed", ctx.channel().remoteAddress());
                ctx.close();
            }
        }
    }
}
