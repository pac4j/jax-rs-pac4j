package org.pac4j.jax.rs.features;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Providers;

import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider.JaxRsContextFactory;
import org.pac4j.jax.rs.pac4j.JaxRsContext;

/**
 *
 * This provides the JAX-RS runtime a way to build a {@link JaxRsContext}
 * adequate for the container.
 *
 * This can be subclassed for specific containers.
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 */
public class JaxRsContextFactoryProvider implements ContextResolver<JaxRsContextFactory> {

    @Context
    private Providers providers;

    @Override
    public JaxRsContextFactory getContext(Class<?> type) {
        return context -> new JaxRsContext(getProviders(), context);
    }

    protected Providers getProviders() {
        assert providers != null;
        return providers;
    }

    /**
     * We need to provide a factory because it is not possible to get the
     * {@link ContainerRequestContext} injected directly here...
     */
    @FunctionalInterface
    public interface JaxRsContextFactory {
        JaxRsContext provides(ContainerRequestContext context);
    }
}
