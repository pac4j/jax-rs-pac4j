package org.pac4j.jax.rs.servlet.features;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.jax.rs.features.JaxRsSessionStoreProvider;
import org.pac4j.jax.rs.servlet.pac4j.ServletSessionStoreFactory;

/**
 * Extends {@link JaxRsSessionStoreProvider} to provide the configured
 * {@link SessionStore} or the default implementation for servlet-based
 * containers
 *
 * @see JaxRsSessionStoreProvider
 * @author Michael Kohlsche
 * @since 5.0.0
 *
 */
public class ServletSessionStoreProvider extends JaxRsSessionStoreProvider {

    public ServletSessionStoreProvider(Config config) {
        super(config);
        config.setSessionStoreFactory(ServletSessionStoreFactory.INSTANCE);
    }

}
