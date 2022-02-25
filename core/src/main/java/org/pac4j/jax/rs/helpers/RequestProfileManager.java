package org.pac4j.jax.rs.helpers;

import org.pac4j.core.context.session.SessionStore;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager;

/**
 * @author Victor Noel
 * @since 2.2.0
 */
public class RequestProfileManager {

    private final JaxRsContext context;
    private final SessionStore sessionStore;

    public RequestProfileManager(JaxRsContext context, SessionStore sessionStore) {
        this.context = context;
        this.sessionStore = sessionStore;
    }

    public JaxRsProfileManager profileManager() {
        return new JaxRsProfileManager(context, sessionStore);
    }
}
