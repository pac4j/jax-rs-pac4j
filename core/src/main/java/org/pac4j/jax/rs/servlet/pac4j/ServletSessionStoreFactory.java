package org.pac4j.jax.rs.servlet.pac4j;

import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.context.session.SessionStoreFactory;

/**
 * {@link SessionStoreFactory} implementation that provides a singleton instance of
 * a {@link ServletSessionStore} for servlet-based environments.
 * <p>
 *  This factory returns the same {@link ServletSessionStore} instance for all calls
 *  to {@link ServletSessionStoreFactory#newSessionStore}, effectively sharing the session store across the application.
 * </p>
 *
 * @since 7.0.0
 */
public class ServletSessionStoreFactory implements SessionStoreFactory {

    public static final SessionStoreFactory INSTANCE = new ServletSessionStoreFactory();

    private static final SessionStore SERVLET_SESSION_STORE = new ServletSessionStore();

    /**
     * Returns the shared {@link ServletSessionStore} instance.
     *
     * @param parameters the framework parameters (not used in this implementation)
     * @return the singleton {@link ServletSessionStore} instance
     */
    @Override
    public SessionStore newSessionStore(FrameworkParameters parameters) {
        return SERVLET_SESSION_STORE;
    }
}
