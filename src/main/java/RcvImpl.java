import de.tum.communication.protocol.messages.Message;

import java.util.Map;
import java.util.Optional;

public class RcvImpl implements Rcv<Message, Message> {

    Map<Integer, Rcv<? extends Message, ? super Message>> receiverMap;


    @Override
    public Optional<Message> rcv(Message input) {
        Rcv<? extends Message, ? super Message> receiver = receiverMap.get(0);
        return Optional.of(receiver.rcv(input).get());
    }
}