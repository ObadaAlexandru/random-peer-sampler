package de.tum.sampling.service;

/**
 * Created by Alexandru Obada on 22/05/16.
 */

import de.tum.sampling.entity.Peer;

import java.util.List;

/**
 *  Manages the network view
 *  Merges and persists incoming views
 */
public interface ViewManager {
    void updateView();
    List<Peer> getForPush();
}
