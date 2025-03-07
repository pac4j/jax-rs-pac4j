package org.pac4j.jax.rs.features;

import jakarta.ws.rs.ext.ContextResolver;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.jax.rs.pac4j.NoOpSessionStoreFactory;

/**
 * This class can be used to inject the pac4j {@link SessionStore} in the
 * JAX-RS runtime.
 *
 * This can be subclassed for container specific implementations.
 *
 * @author Michael Kohlsche
 * @since 5.0.0
 */
public class JaxRsSessionStoreProvider implements ContextResolver<SessionStore> {

    protected final Config config;

    public JaxRsSessionStoreProvider(Config config) {
        this.config = config;
        config.setSessionStoreFactory(NoOpSessionStoreFactory.INSTANCE);
    }

    @Override
    public SessionStore getContext(Class<?> type) {
        return this.config.getSessionStoreFactory().newSessionStore(null);
    }

}
