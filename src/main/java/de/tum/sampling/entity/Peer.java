package de.tum.sampling.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.InetAddress;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
@Data
@EqualsAndHashCode(of = "identifier")
@NoArgsConstructor
@Entity
public class Peer {
    @Id
    @Size(min = 64, max = 64)
    private String identifier;
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
    public Peer(String identifier, InetAddress address, Integer port, Long age) {
        this.identifier = identifier;
        this.address = address;
        this.port = port;
        this.age = age;
    }

    public void resetAge() {
        age = 0L;
    }
}