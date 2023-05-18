package org.pac4j.jax.rs.grizzly.features;

import jakarta.ws.rs.core.FeatureContext;

import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.features.Pac4JFeature;

/**
 *
 * Extends {@link Pac4JFeature} to register the default providers for the
 * Grizzly container (without the need for servlet support)
 *
 * @see Pac4JFeature
 * @author Michael Kohlsche
 * @since 5.0.0
 *
 */
public class Pac4JGrizzlyFeature extends Pac4JFeature {

    public Pac4JGrizzlyFeature(Config config) {
        super(config);
    }

    protected boolean registerContextFactoryProvider(FeatureContext context) {
        context.register(GrizzlyJaxRsContextFactoryProvider.class);
        return true;
    }

    protected boolean registerSessionStoreProvider(FeatureContext context) {
        context.register(new GrizzlySessionStoreProvider(config));
        return true;
    }

}
