package de.tum.communication.service;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Alexandru Obada on 12/05/16.
 */

/**
 * Narrows down the possible beans to be injected
 * To avoid ambiguity during dependency injection
 */
@Target({ElementType.FIELD,
        ElementType.METHOD,
        ElementType.TYPE,
        ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface Module {
    Service value();
    enum Service {
        GOSSIP,
        NSE,
        RPS,
        BASE
    }
}