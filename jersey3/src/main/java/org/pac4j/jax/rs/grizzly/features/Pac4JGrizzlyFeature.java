package org.pac4j.jax.rs.grizzly.features;

import jakarta.ws.rs.core.FeatureContext;

import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.features.Pac4JDefaultFeature;

/**
 *
 * Extends {@link Pac4JDefaultFeature} to register the default providers for the
 * Grizzly container (without the need for servlet support)
 *
 * @see Pac4JDefaultFeature
 * @author Michael Kohlsche
 * @since 5.0.0
 *
 */
public class Pac4JGrizzlyFeature extends Pac4JDefaultFeature {

    public Pac4JGrizzlyFeature(Config config) {
        super(config);
    }

    @Override
    protected boolean registerSessionStoreProvider(Config config, FeatureContext context) {
        context.register(new GrizzlySessionStoreProvider(config));
        return true;
    }

    @Override
    public boolean registerContextFactoryProvider(Config config, FeatureContext context) {
        context.register(new GrizzlyJaxRsContextFactoryProvider());
        return true;
    }


}
