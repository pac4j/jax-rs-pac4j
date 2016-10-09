package org.pac4j.jax.rs.features;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider.JaxRsContextFactory;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsSessionStore;

/**
 * 
 * This provides to the JAX-RS runtime a way to build a {@link JaxRsContext} adequate for the container.
 * 
 * This is the generic implementation and will not support session management (and hence won't support pac4j indirect
 * clients).
 * 
 * This can be subclassed for specific containers.
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class JaxRsContextFactoryProvider implements ContextResolver<JaxRsContextFactory> {

    @Context
    protected Providers providers;

    protected final Config config;

    public JaxRsContextFactoryProvider(Config config) {
        assert config != null;
        this.config = config;
    }

    @Override
    public JaxRsContextFactory getContext(Class<?> type) {
        assert providers != null;
        return context -> new JaxRsContext(providers, context, (JaxRsSessionStore) config.getSessionStore());
    }

    @FunctionalInterface
    public interface JaxRsContextFactory {
        JaxRsContext provides(ContainerRequestContext context);
    }

}
