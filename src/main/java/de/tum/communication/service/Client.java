package de.tum.communication.service;

import de.tum.communication.protocol.messages.Message;
import io.netty.channel.ChannelFuture;
import lombok.NonNull;

import java.net.InetAddress;


/**
 * Created by Alexandru Obada on 12/05/16.
 */

/**
 *  Communication interface to other modules
 */
public interface Client extends Sender<Message, Void>, ReceiverAware<Message> {
//    ChannelFuture connect(@NonNull InetAddress host, @NonNull Integer port);
//    ChannelFuture connect();
}