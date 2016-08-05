package de.tum.sampling.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import de.tum.communication.protocol.Token;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Nicolas Frinker on 04/08/16.
 */

/**
 * Keep track of generated tokens.
 * Old tokens will be invalidated and deleted.
 */
@Component
@Slf4j
public class TokenRepoImpl implements TokenRepo {
    private final List<Token> tokens = new ArrayList<>();

    @Override
    public Token newToken() {
        Token token = new Token();
        while (this.tokens.contains(token)) {
            log.info("Generated identical token. Redo.");
            token = new Token();
        }
        tokens.add(token);
        return token;
    }

    @Override
    public boolean checkToken(Token token) {

        for (Token repotoken : this.tokens) {
            if (repotoken.equals(token)) {
                if (repotoken.isValid()) {
                    return true;
                } else {
                    log.info("Invalidating old token from " + repotoken.getCreationDate());
                    this.removeToken(repotoken);
                }
            }
        }

        return false;
    }

    @Override
    public void removeToken(Token token) {
        this.tokens.remove(token);
    }
}
