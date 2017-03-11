package org.pac4j.jax.rs.features;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider.JaxRsContextFactory;
import org.pac4j.jax.rs.helpers.ProvidersHelper;
import org.pac4j.jax.rs.pac4j.JaxRsContext;

/**
 * 
 * This provides to the JAX-RS runtime a way to build a {@link JaxRsContext} adequate for the container.
 * 
 * This is the generic implementation and will not support session management (and hence won't support pac4j
 * {@link IndirectClient}).
 * 
 * This can be subclassed for specific containers.
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class JaxRsContextFactoryProvider implements ContextResolver<JaxRsContextFactory> {

    @Context
    private Providers providers;

    @Override
    public JaxRsContextFactory getContext(Class<?> type) {
        return context -> new JaxRsContext(getProviders(), context, getConfig().getSessionStore());
    }

    protected Providers getProviders() {
        assert providers != null;
        return providers;
    }

    protected Config getConfig() {
        return ProvidersHelper.getContext(providers, Config.class);
    }

    /**
     * We need to provide a factory because it is not possible to get the {@link ContainerRequestContext} injected
     * directly here...
     */
    @FunctionalInterface
    public interface JaxRsContextFactory {
        JaxRsContext provides(ContainerRequestContext context);
    }
}
