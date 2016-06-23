package de.tum.sampling.service;

import com.google.common.base.CharMatcher;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Bytes;
import de.tum.communication.protocol.messages.Message;
import de.tum.communication.protocol.messages.NseEstimateMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.truth.Truth.assertThat;

@Slf4j
public class NseTestServer {

    ExecutorService executorService;
    ServerSocketChannel serverSocketChannel;
    private volatile boolean stopped;
    int port;
    private static final String NSE_QUERY_MESSAGE = "00 04 02 08";
    private final byte[] response;

    @Getter
    private AtomicInteger numReceivedMessages;

    public NseTestServer(int port, byte[] response) throws IOException {
        this.port = port;
        this.response = response;
        executorService = Executors.newSingleThreadExecutor();
        serverSocketChannel = ServerSocketChannel.open();
        numReceivedMessages = new AtomicInteger();
    }

    public void start() throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(port));
        stopped = false;
        log.info("Test nse server started ...");
        while (!stopped) {
            SocketChannel channel = serverSocketChannel.accept();
            log.debug("Connection received");
            ConnectionHandler connectionHandler = new ConnectionHandler(channel);
            executorService.submit(connectionHandler);
        }
        log.info("Test nse server stopping ... ");
    }

    public synchronized void stop() {
        stopped = true;
        executorService.shutdown();
    }

    @AllArgsConstructor
    private class ConnectionHandler implements Runnable {
        private SocketChannel channel;

        @Override
        public void run() {
            String expectedMessage = CharMatcher.WHITESPACE.removeFrom(NSE_QUERY_MESSAGE);
            byte[] nseQueryBytes = BaseEncoding.base16().decode(CharMatcher.WHITESPACE.removeFrom(NSE_QUERY_MESSAGE));

            ByteBuffer byteBuffer = ByteBuffer.allocate(48);
            try {
                int bytesRead = channel.read(byteBuffer);
                byteBuffer.flip();
                /*
                    check if the received messages matches the expectations
                 */
                assertThat(bytesRead).isEqualTo(nseQueryBytes.length);

                byte[] receivedData = new byte[bytesRead];
                byteBuffer.get(receivedData);
                /*
                    Check if the bytes are correctly received
                 */
                String receivedMessage = BaseEncoding.base16().encode(receivedData).toUpperCase();
                assertThat(receivedMessage).isEqualTo(expectedMessage);

                // Increment the number of received messages
                numReceivedMessages.incrementAndGet();

                NseEstimateMessage.builder()
                        .estimatedPeerNumbers(15)
                        .estimatedStandardDeviation(2)
                        .build();

                channel.write(ByteBuffer.wrap(response));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
