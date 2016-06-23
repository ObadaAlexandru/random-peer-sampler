package de.tum.communication.protocol.messages;


import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import de.tum.communication.protocol.MessageType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

/**
 * Created by Alexandru Obada on 11/05/16.
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class NseEstimateMessage extends Message {

    private static final short PAYLOAD_LENGTH = 8;

    private int estimatedPeerNumber;
    private int estimatedStandardDeviation;

    @Builder
    public NseEstimateMessage(@NonNull  Integer estimatedPeerNumbers, @NonNull Integer estimatedStandardDeviation) {
        super((short) (WORD_LENGTH + PAYLOAD_LENGTH), MessageType.NSE_ESTIMATE);
        this.estimatedPeerNumber = estimatedPeerNumbers;
        this.estimatedStandardDeviation = estimatedStandardDeviation;
    }

    @Override
    public List<Byte> getBytes() {
        byte[] headerBytes = getHeaderBytes();
        byte[] estimatedPeerNumbersBytes = Ints.toByteArray(estimatedPeerNumber);
        byte[] estimatedStandardDeviationBytes = Ints.toByteArray(estimatedStandardDeviation);
        return Bytes.asList(Bytes.concat(headerBytes, estimatedPeerNumbersBytes, estimatedStandardDeviationBytes));
    }
}
