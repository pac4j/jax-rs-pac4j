package org.pac4j.jax.rs.grizzly.features;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.jax.rs.features.JaxRsSessionStoreProvider;
import org.pac4j.jax.rs.grizzly.pac4j.GrizzlySessionStore;

/**
 * 
 * Extends {@link JaxRsSessionStoreProvider} to provide the configured
 * {@link SessionStore} or the default implementation for the Grizzly container
 * (without the need for servlet support)
 * 
 * @see JaxRsSessionStoreProvider
 * @author Michael Kohlsche
 * @since 5.0.0
 * 
 */
public class GrizzlySessionStoreProvider extends JaxRsSessionStoreProvider {

    public GrizzlySessionStoreProvider(Config config) {
        super(config);
    }

    @Override
    public SessionStore getContext(Class<?> type) {
        return (config.getSessionStore() != null) ? config.getSessionStore() : GrizzlySessionStore.INSTANCE;
    }

}
