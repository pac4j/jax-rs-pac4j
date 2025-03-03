package org.pac4j.jax.rs.features;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import org.pac4j.core.adapter.FrameworkAdapter;
import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.filters.DefaultJaxRsHttpActionAdapter;
import org.pac4j.jax.rs.pac4j.DefaultJaxRsWebContextFactory;

/**
 * This feature can be used to register the default set of necessary providers.
 *
 * This should be subclassed for container specific implementations and override
 * the necessary registration methods.
 *
 * @author Michael Kohlsche
 * @since 5.0.0
 */
public abstract class Pac4JDefaultFeature implements Feature {

    protected final Config config;

    protected Pac4JDefaultFeature(Config config) {
        this.config = config;
        this.config.setHttpActionAdapterIfUndefined(DefaultJaxRsHttpActionAdapter.INSTANCE);
        this.config.setWebContextFactoryIfUndefined(DefaultJaxRsWebContextFactory.INSTANCE);
    }

    boolean registerConfigProvider(Config config, FeatureContext context) {
        context.register(new JaxRsConfigProvider(config));
        return true;
    }

    abstract protected boolean registerSessionStoreProvider(Config config , FeatureContext context);

    abstract protected boolean registerContextFactoryProvider(Config config, FeatureContext context);

    @Override
    public boolean configure(FeatureContext context) {
        return registerConfigProvider(this.config, context)
            && registerSessionStoreProvider(this.config, context)
            && registerContextFactoryProvider(this.config, context);
    }
}
