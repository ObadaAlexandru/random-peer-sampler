package de.tum.sampling.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
@Data
@Entity
public class Peer {
    @Id
    private String identifier;
    private String address;
    private Short port;
}
