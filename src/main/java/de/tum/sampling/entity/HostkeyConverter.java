package de.tum.sampling.entity;

import com.google.common.io.BaseEncoding;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Nicolas Frinker on 28/05/16.
 */
@Converter
public class HostkeyConverter implements AttributeConverter<PublicKey, String> {
    @Override
    public String convertToDatabaseColumn(PublicKey key) {
        return BaseEncoding.base64().encode(key.getEncoded());
    }

    @Override
    public PublicKey convertToEntityAttribute(String dbData) {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(BaseEncoding.base64().decode(dbData)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            //TODO write custom exception
            e.printStackTrace();
            throw new RuntimeException("Inconsistent hostkey");
        }
    }
}
