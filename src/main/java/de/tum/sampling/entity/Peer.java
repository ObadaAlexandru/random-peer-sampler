package de.tum.sampling.entity;

import com.google.common.io.BaseEncoding;
import lombok.*;

import javax.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    @Convert(converter = HostkeyConverter.class)
    @Column(length = 4096)
    private PublicKey hostkey;
    @NonNull
    @Convert(converter = AddressConverter.class)
    private InetAddress address;
    @NotNull
    @Min(1024)
    @Max(65535)
    private Integer port;
    @NotNull
    private PeerType peerType;

    @Builder
    public Peer(PeerType peerType, PublicKey hostkey, InetAddress address, Integer port) {
        this.peerType = peerType;
        this.hostkey = hostkey;
        this.address = address;
        this.port = port;
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