package de.tum.sampling.entity;

import de.tum.communication.protocol.Token;

/**
 * Created by Nicolas Frinker on 04/08/16.
 */
public interface TokenRepo {

    /**
     * Get new token for new push
     *
     * @return
     */
    public Token newToken();

    /**
     * Check, if given token is valid and received view should be accepted
     *
     * @param token
     * @return
     */
    public boolean checkToken(Token token);

    /**
     * Remove token from repo
     *
     * @param token
     */
    public void removeToken(Token token);
}
