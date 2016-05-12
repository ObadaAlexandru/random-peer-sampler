package de.tum.communication.protocol;


import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
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

    private int estimatedPeerNumbers;
    private int estimatedStandardDeviation;

    @Builder
    public NseEstimateMessage(@NonNull  Integer estimatedPeerNumbers, @NonNull Integer estimatedStandardDeviation) {
        super((short) (WORD_LENGTH + PAYLOAD_LENGTH), MessageType.NSE_ESTIMATE);
        this.estimatedPeerNumbers = estimatedPeerNumbers;
        this.estimatedStandardDeviation = estimatedStandardDeviation;
    }

    @Override
    public List<Byte> getBytes() {
        byte[] headerBytes = getHeaderBytes();
        byte[] estimatedPeerNumbersBytes = Ints.toByteArray(estimatedPeerNumbers);
        byte[] estimatedStandardDeviationBytes = Ints.toByteArray(estimatedStandardDeviation);
        return Bytes.asList(Bytes.concat(headerBytes, estimatedPeerNumbersBytes, estimatedStandardDeviationBytes));
    }
}
