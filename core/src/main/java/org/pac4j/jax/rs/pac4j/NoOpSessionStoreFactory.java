package org.pac4j.jax.rs.pac4j;

import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.context.session.SessionStoreFactory;

import java.util.Optional;

/**
 * A no-operation implementation of {@link SessionStoreFactory} that returns a
 * {@link NoOpSessionStore}. This factory is used when session management is not required (e.g. direct clients)
 *
 * @since 7.0.0
 */
public class NoOpSessionStoreFactory implements SessionStoreFactory {

    public static final SessionStoreFactory INSTANCE = new NoOpSessionStoreFactory();

    @Override
    public SessionStore newSessionStore(FrameworkParameters frameworkParameters) {
        return new NoOpSessionStore();
    }

    /**
     * A no-operation implementation of {@link SessionStore} that does not store any session data.
     */
    public static class NoOpSessionStore implements SessionStore {

        @Override
        public Optional<String> getSessionId(WebContext webContext, boolean b) {
            return Optional.empty();
        }

        @Override
        public Optional<Object> get(WebContext webContext, String s) {
            return Optional.empty();
        }

        @Override
        public void set(WebContext webContext, String s, Object o) {

        }

        @Override
        public boolean destroySession(WebContext webContext) {
            return true;
        }

        @Override
        public Optional<Object> getTrackableSession(WebContext webContext) {
            return Optional.empty();
        }

        @Override
        public Optional<SessionStore> buildFromTrackableSession(WebContext webContext, Object o) {
            return Optional.empty();
        }

        @Override
        public boolean renewSession(WebContext webContext) {
            return true;
        }
    }
}
