package org.pac4j.jax.rs.features;

import org.junit.Test;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.jax.rs.pac4j.NoOpSessionStoreFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JaxRsSessionStoreProviderTest {

    @Test
    public void should_provide_session_store_when_session_store_factory_is_set() {
        Config config = new Config();
        config.setSessionStoreFactory(NoOpSessionStoreFactory.INSTANCE);

        JaxRsSessionStoreProvider jaxRsSessionStoreProvider = new JaxRsSessionStoreProvider(config);
        SessionStore sessionStore = jaxRsSessionStoreProvider.getContext(null);

        assertNotNull(sessionStore);
        assertEquals(NoOpSessionStoreFactory.NoOpSessionStore.class, sessionStore.getClass());
    }

    @Test(expected = TechnicalException.class)
    public void should_throw_when_session_store_factory_is_not_set() {
        Config config = new Config();
        JaxRsSessionStoreProvider jaxRsSessionStoreProvider = new JaxRsSessionStoreProvider(config);
        jaxRsSessionStoreProvider.getContext(null);
    }
}
