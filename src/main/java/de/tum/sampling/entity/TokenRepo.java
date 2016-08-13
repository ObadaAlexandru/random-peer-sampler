package de.tum.sampling.entity;

import de.tum.communication.protocol.Token;

/**
 * Created by Nicolas Frinker on 04/08/16.
 *
 * Stores and tracks the created tokens
 */
public interface TokenRepo {

    /**
     * Get new token for new push
     *
     * @return
     */
    Token newToken();

    /**
     * Check, if given token is valid and received view should be accepted
     *
     * @param token
     * @return
     */
    boolean checkToken(Token token);

    /**
     * Remove token from repo
     *
     * @param token
     */
    void removeToken(Token token);
}
