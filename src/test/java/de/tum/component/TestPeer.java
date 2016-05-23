package de.tum.component;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
@Data
@EqualsAndHashCode(of = "identifier")
public class TestPeer {
    private String identifier;
    private String address;
    private Integer port;
    private Long age;
}
