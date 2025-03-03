package org.pac4j.jax.rs.servlet.pac4j;

import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.context.session.SessionStoreFactory;

public class ServletSessionStoreFactory implements SessionStoreFactory {

    public static final SessionStoreFactory INSTANCE = new ServletSessionStoreFactory();

    private static final SessionStore SERVLET_SESSION_STORE = new ServletSessionStore();

    @Override
    public SessionStore newSessionStore(FrameworkParameters parameters) {
        return SERVLET_SESSION_STORE;
    }
}
