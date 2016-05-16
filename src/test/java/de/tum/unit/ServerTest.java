package de.tum.unit;

import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by Alexandru Obada on 15/05/16.
 */
@RunWith(JUnit4.class)
public class ServerTest {

    @Test
    public void test() {
        // given
//        EmbeddedChannel embeddedChannel = new EmbeddedChannel();
//
//        // and - no SSL handler
//        assertThat(embeddedChannel.pipeline().get(SslHandler.class), is(nullValue()));
//
//        // when - first part of a 5-byte handshake message
//        embeddedChannel.writeInbound(Unpooled.wrappedBuffer(new byte[]{
//                22, // handshake
//                3,  // major version
//                1,
//                0,
//                5   // package length (5-byte)
//        }));
//
//        // then - should add SSL handlers first
//        assertThat(embeddedChannel.pipeline().names(), contains(
//                "SslHandler#0",
//                "HttpProxyUnificationHandler#0",
//                "EmbeddedChannel$LastInboundHandler#0",
//                "DefaultChannelPipeline$TailContext#0"
//        ));
    }
}
