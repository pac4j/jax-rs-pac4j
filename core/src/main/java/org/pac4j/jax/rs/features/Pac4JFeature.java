package org.pac4j.jax.rs.features;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.pac4j.core.config.Config;

/**
 * This feature can be used to register the default set of necessary providers.
 * 
 * This should be subclassed for container specific implementations and override
 * the necessary registration methods.
 * 
 * @author Michael Kohlsche
 * @since 5.0.0
 */
public class Pac4JFeature implements Feature {

    protected final Config config;

    public Pac4JFeature(Config config) {
        this.config = config;
    }

    @Override
    public boolean configure(FeatureContext context) {
        return registerConfigProvider(context) && registerSessionStoreProvider(context)
                && registerContextFactoryProvider(context);
    }

    protected boolean registerConfigProvider(FeatureContext context) {
        context.register(new JaxRsConfigProvider(config));
        return true;
    }

    protected boolean registerContextFactoryProvider(FeatureContext context) {
        context.register(JaxRsContextFactoryProvider.class);
        return true;
    }

    protected boolean registerSessionStoreProvider(FeatureContext context) {
        context.register(new JaxRsSessionStoreProvider(config));
        return true;
    }

}
