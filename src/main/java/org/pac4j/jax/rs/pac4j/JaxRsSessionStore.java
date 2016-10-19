package org.pac4j.jax.rs.pac4j;

import org.pac4j.core.context.session.SessionStore;

/**
 * 
 * Note: this could actually be directly included in pac4j, see https://github.com/pac4j/pac4j/issues/711
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public interface JaxRsSessionStore extends SessionStore<JaxRsContext> {

    void renewSession(JaxRsContext context);
}
