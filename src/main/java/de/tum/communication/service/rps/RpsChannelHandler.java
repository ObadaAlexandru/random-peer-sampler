package de.tum.communication.service.rps;

import de.tum.communication.protocol.Message;
import de.tum.communication.service.Receiver;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.PackagePrivate;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Alexandru Obada on 16/05/16.
 */
@Slf4j
@NoArgsConstructor
@ChannelHandler.Sharable
class RpsChannelHandler extends SimpleChannelInboundHandler<Message> {

    @PackagePrivate
    @Setter(AccessLevel.PACKAGE)
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
