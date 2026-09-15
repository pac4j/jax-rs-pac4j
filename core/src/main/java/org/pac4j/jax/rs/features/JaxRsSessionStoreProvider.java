package org.pac4j.jax.rs.features;

import java.util.Optional;

import jakarta.ws.rs.ext.ContextResolver;

import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.TechnicalException;

/**
 * This class can be used to inject the pac4j {@link SessionStore} in the
 * JAX-RS runtime. The session store factory should be set in the provided config.
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
    }

    @Override
    public SessionStore getContext(Class<?> type) {
        return Optional.ofNullable(this.config.getSessionStoreFactory())
            .orElseThrow(() ->
                new TechnicalException("No session store factory has been set in the config"))
            .newSessionStore(null);
    }

}
