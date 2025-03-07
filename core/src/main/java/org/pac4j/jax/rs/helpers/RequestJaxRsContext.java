package org.pac4j.jax.rs.helpers;

import java.util.Optional;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.ext.Providers;

import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider.JaxRsContextFactory;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager.Pac4JSecurityContext;

/**
 * @author Victor Noel
 * @since 2.2.0
 */
public class RequestJaxRsContext {

    private final Providers providers;
    private final ProvidersContext providersContext;
    private final ContainerRequestContext requestContext;

    public RequestJaxRsContext(Providers providers, ContainerRequestContext requestContext) {
        this.providers = providers;
        this.providersContext = new ProvidersContext(providers);
        this.requestContext = requestContext;
    }

    public Optional<JaxRsContext> context() {
        return new RequestPac4JSecurityContext(requestContext).context().map(Pac4JSecurityContext::getContext);
    }

    public JaxRsContext contextOrNew() {
        return context().orElse(providersContext.resolveNotNull(JaxRsContextFactory.class).provides(requestContext));
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
