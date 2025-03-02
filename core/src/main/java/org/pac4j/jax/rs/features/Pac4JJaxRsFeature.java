package org.pac4j.jax.rs.features;

import jakarta.ws.rs.core.FeatureContext;

import org.pac4j.core.config.Config;

/**
 *
 * Extends {@link Pac4JDefaultFeature} to register the default providers for
 * JAX-RS-based containers
 *
 * @see Pac4JDefaultFeature
 * @author Michael Kohlsche
 * @since 5.0.0
 *
 */
public class Pac4JJaxRsFeature extends Pac4JDefaultFeature {

    public Pac4JJaxRsFeature(Config config) {
        super(config);
    }

    @Override
    public boolean registerSessionStoreProvider(Config config, FeatureContext context) {
        context.register(new JaxRsSessionStoreProvider(config));
        return true;
    }

    @Override
    public boolean registerContextFactoryProvider(Config config, FeatureContext context) {
        context.register(JaxRsContextFactoryProvider.class);
        return true;
    }

}
