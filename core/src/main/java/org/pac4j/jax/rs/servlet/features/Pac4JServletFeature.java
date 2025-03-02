package org.pac4j.jax.rs.servlet.features;

import jakarta.ws.rs.core.FeatureContext;

import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.features.Pac4JDefaultFeature;

/**
 *
 * Extends {@link Pac4JDefaultFeature} to register the default providers for
 * servlet-based containers
 *
 * @see Pac4JDefaultFeature
 * @author Michael Kohlsche
 * @since 5.0.0
 *
 */
public class Pac4JServletFeature extends Pac4JDefaultFeature {

    public Pac4JServletFeature(Config config) {
        super(config);
    }

    @Override
    public boolean registerContextFactoryProvider(Config config, FeatureContext context) {
        context.register(ServletJaxRsContextFactoryProvider.class);
        return true;
    }

    @Override
    public boolean registerSessionStoreProvider(Config config, FeatureContext context) {
        context.register(new ServletSessionStoreProvider(config));
        return true;
    }

}
