package de.tum.sampling.entity;

import com.google.common.io.BaseEncoding;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
@Data
@EqualsAndHashCode(of = "hostkey")
@NoArgsConstructor
@Entity
public class Peer {
    @Id
    @NonNull
    @Convert(converter = HostkeyConverter.class)
    @Column(length=4096)
    private PublicKey hostkey;
    @NonNull
    @Convert(converter = AddressConverter.class)
    private InetAddress address;
    @NotNull
    @Min(1024)
    @Max(65535)
    private Integer port;
    @Min(0)
    @NotNull
    private Long age;

    @Builder
    public Peer(PublicKey hostkey, InetAddress address, Integer port, Long age) {
        this.hostkey = hostkey;
        this.address = address;
        this.port = port;
        this.age = age;
    }

    public void resetAge() {
        age = 0L;
    }

    public String getIdentifier() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(this.hostkey.getEncoded());
            return BaseEncoding.base16().encode(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("SHA256 not available!");
        }
    }
}