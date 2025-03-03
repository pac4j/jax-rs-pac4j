package org.pac4j.jax.rs.grizzly.pac4j;

import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.context.session.SessionStoreFactory;

public class GrizzlySessionStoreFactory implements SessionStoreFactory {

    public static final SessionStoreFactory INSTANCE = new GrizzlySessionStoreFactory();

    private static final SessionStore GRIZZLY_SESSION_STORE = new GrizzlySessionStore();

    @Override
    public SessionStore newSessionStore(FrameworkParameters parameters) {
        return GRIZZLY_SESSION_STORE;
    }
}
