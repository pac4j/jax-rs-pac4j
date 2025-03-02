package org.pac4j.jax.rs.pac4j;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.ext.Providers;
import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.jax.rs.helpers.ProvidersContext;

public class JaxRsFrameworkParameters implements FrameworkParameters {

    private final Providers providers;
    private final ProvidersContext providersContext;
    private final ContainerRequestContext requestContext;

    public JaxRsFrameworkParameters(Providers providers, ContainerRequestContext requestContext) {
        this.providers = providers;
        this.providersContext = new ProvidersContext(providers);
        this.requestContext = requestContext;
    }

    public Providers getProviders() {
        return providers;
    }

    public ProvidersContext getProvidersContext() {
        return providersContext;
    }

    public ContainerRequestContext getRequestContext() {
        return requestContext;
    }
}
