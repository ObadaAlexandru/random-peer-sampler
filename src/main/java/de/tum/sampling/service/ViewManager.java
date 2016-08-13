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

    /**
     * Computes the new dynamic view as specified in Brahms
     */
    void updateView();

    /**
     * Randomly selects a subset of the current dynamic view
     * According to Brahms this implements Limited pushes
     *
     * @return a subset of the dynamic view
     */
    List<Peer> getForPush();

    /**
     * Update dynamic view size
     * @param viewSize
     */
    void setViewSize(int viewSize);

    /**
     *
     * @return the current dynamic view size
     */
    int getViewSize();
}
