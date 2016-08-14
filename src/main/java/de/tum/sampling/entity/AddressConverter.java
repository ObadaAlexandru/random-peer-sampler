package de.tum.sampling.entity;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Alexandru Obada on 22/05/16.
 *
 *  Used by Hibernate to convert {@link InetAddress} to and from {@link String}
 *  Needed for persistence
 */
@Slf4j
@Converter
public class AddressConverter implements AttributeConverter<InetAddress, String> {
    @Override
    public String convertToDatabaseColumn(InetAddress address) {
        return address.getHostAddress();
    }

    @Override
    public InetAddress convertToEntityAttribute(String dbData) {
        try {
            return InetAddress.getByName(dbData);
        } catch (UnknownHostException e) {
            //TODO write custom exception
            e.printStackTrace();
            throw new RuntimeException("Inconsistent address");
        }
    }
}
