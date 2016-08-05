package de.tum.communication.protocol;

import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.common.primitives.Bytes;

/**
 * Created by Nicolas Frinker on 04/08/16.
 */

/**
 * A token used in RpsPushMessages and returned in RpsViewMessages
 *
 * This allows to make sure we only receive answers from valid peers in the
 * network, if we assume the communication with the other peers is confident
 */
public class Token implements ByteSerializable {
    public static final short TOKEN_LENGTH = 64;
    private static final int TIMEOUT_MS = 10 * 60 * 1000;
    private Date creationdate;
    private byte[] token = new byte[TOKEN_LENGTH];

    public Token(List<Byte> token) {
        this.token = Bytes.toArray(token);
    }

    public Token() {
        // Generate new token
        Random rand = new Random();
        rand.nextBytes(token);
        this.creationdate = new Date();
    }

    /**
     * Get byte array to be sent in a Message
     */
    @Override
    public List<Byte> getBytes() {
        return Bytes.asList(this.token);
    }

    public Date getCreationDate() {
        return this.creationdate;
    }

    public short getSize() {
        return TOKEN_LENGTH;
    }

    /**
     * Is this token still valid?
     *
     * @return
     */
    public boolean isValid() {
        if (this.creationdate == null || (new Date()).getTime() - this.creationdate.getTime() > TIMEOUT_MS) {
            return false;
        }
        return true;
    }
}
