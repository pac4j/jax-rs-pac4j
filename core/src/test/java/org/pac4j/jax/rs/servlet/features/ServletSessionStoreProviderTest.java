package org.pac4j.jax.rs.servlet.features;

import org.junit.Test;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.jax.rs.pac4j.NoOpSessionStoreFactory;
import org.pac4j.jax.rs.servlet.pac4j.ServletSessionStore;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ServletSessionStoreProviderTest {

    @Test
    public void should_provide_session_store_from_user_provided_session_store_factory() {
        Config config = new Config();
        config.setSessionStoreFactory(NoOpSessionStoreFactory.INSTANCE);

        ServletSessionStoreProvider sessionStoreProvider = new ServletSessionStoreProvider(config);
        SessionStore sessionStore = sessionStoreProvider.getContext(null);

        assertNotNull(sessionStore);
        assertTrue(sessionStore instanceof NoOpSessionStoreFactory.NoOpSessionStore);
    }

    @Test
    public void should_provide_default_servlet_session_store_when_not_configured() {
        Config config = new Config();

        ServletSessionStoreProvider sessionStoreProvider = new ServletSessionStoreProvider(config);
        SessionStore sessionStore = sessionStoreProvider.getContext(null);

        assertNotNull(sessionStore);
        assertTrue(sessionStore instanceof ServletSessionStore);
    }
}
