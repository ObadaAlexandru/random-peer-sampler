package de.tum.sampling.service;

import java.util.List;

/**
 * Created by Alexandru Obada on 22/05/16.
 */

import de.tum.sampling.entity.Peer;

/**
 *  Manages the network view
 *  Merges and persists incoming views
 */
public interface ViewManager {
    void updateView();
    List<Peer> getForPush();
    void setViewSize(int viewSize);
    int getViewSize();
}
