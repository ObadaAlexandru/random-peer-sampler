package de.tum.sampling.entity;

import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.google.common.io.BaseEncoding;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Wither;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
@Data
@Wither
@EqualsAndHashCode(of = "hostkey")
@NoArgsConstructor
@Entity
@ToString(exclude="hostkey")
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