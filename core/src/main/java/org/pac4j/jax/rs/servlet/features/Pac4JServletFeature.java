package org.pac4j.jax.rs.servlet.features;

import javax.ws.rs.core.FeatureContext;

import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.features.Pac4JFeature;

/**
 * 
 * Extends {@link Pac4JFeature} to register the default providers for
 * servlet-based containers
 * 
 * @see Pac4JFeature
 * @author Michael Kohlsche
 * @since 5.0.0
 * 
 */
public class Pac4JServletFeature extends Pac4JFeature {

    public Pac4JServletFeature(Config config) {
        super(config);
    }

    protected boolean registerContextFactoryProvider(FeatureContext context) {
        context.register(ServletJaxRsContextFactoryProvider.class);
        return true;
    }

    protected boolean registerSessionStoreProvider(FeatureContext context) {
        context.register(new ServletSessionStoreProvider(config));
        return true;
    }

}
