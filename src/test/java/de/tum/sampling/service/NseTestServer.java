package de.tum.sampling.service;

import com.google.common.base.CharMatcher;
import com.google.common.io.BaseEncoding;
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

/**
 * Emulates NSE module
 */
@Slf4j
public class NseTestServer {

    private ExecutorService executorService;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean running;
    private volatile boolean stopped = false;
    @Getter
    private int port;
    private static final String NSE_QUERY_MESSAGE = "00 04 02 08";
    private byte[] response;

    private AtomicInteger numReceivedMessages;

    public NseTestServer() {
        executorService = Executors.newFixedThreadPool(2);
        numReceivedMessages = new AtomicInteger();
    }

    public NseTestServer(int port, byte[] response) throws IOException {
        this();
        this.port = port;
        this.response = response;
    }

    public void setPort(int port) {
        if (isRunning()) {
            throw new RuntimeException("Parameters changed while running");
        }
        this.port = port;
    }

    public void setResponse(byte[] response) {
        if (isRunning()) {
            throw new RuntimeException("Parameters changed while running");
        }
        this.response = response;
    }

    public Integer getNumReceivedQueries() {
        return numReceivedMessages.get();
    }

    public Integer resetQueryCounter() {
        return numReceivedMessages.getAndSet(0);
    }

    /**
     * doesn't block
     */
    public void start() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to open server channel");
        }
        executorService.submit(() -> {
            try {
                serverSocketChannel.bind(new InetSocketAddress(port));
                stopped = false;
                running = true;
                log.info("Test nse server started ...");
                while (!stopped) {
                    SocketChannel channel = serverSocketChannel.accept();
                    log.debug("Connection received");
                    ConnectionHandler connectionHandler = new ConnectionHandler(channel);
                    executorService.submit(connectionHandler);
                }
                serverSocketChannel.close();
                log.info("Test nse server stopping ... ");
                running = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void stop() {
        stopped = true;
    }

    public synchronized boolean isRunning() {
        return running;
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

                channel.write(ByteBuffer.wrap(response));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
