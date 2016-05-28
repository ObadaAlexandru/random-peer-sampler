package de.tum.component;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by Alexandru Obada on 22/05/16.
 */
@Data
@EqualsAndHashCode(of = "hostkey")
public class TestPeer {
    private String hostkey;
    private String address;
    private Integer port;
    private Long age;
}
