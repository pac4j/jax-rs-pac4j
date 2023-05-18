package org.pac4j.jax.rs.features;

import jakarta.ws.rs.ext.ContextResolver;

import org.pac4j.core.config.Config;

/**
 *
 * This class can be used to inject the pac4j {@link Config} in the JAX-RS
 * runtime.
 *
 * @author Victor Noel - Linagora
 * @since 2.0.0
 */
public class JaxRsConfigProvider implements ContextResolver<Config> {

    private final Config config;

    public JaxRsConfigProvider(Config config) {
        this.config = config;
    }

    @Override
    public Config getContext(Class<?> type) {
        return config;
    }

}
