package org.pac4j.jax.rs.helpers;

import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Providers;

import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider.JaxRsContextFactory;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager.Pac4JSecurityContext;

/**
 * @author Victor Noel
 * @since 2.2.0
 */
public class RequestJaxRsContext {

    private final ProvidersContext providers;
    private final ContainerRequestContext context;

    public RequestJaxRsContext(Providers providers, ContainerRequestContext context) {
        this.providers = new ProvidersContext(providers);
        this.context = context;
    }

    public Optional<JaxRsContext> context() {
        return new RequestPac4JSecurityContext(context).context().map(Pac4JSecurityContext::getContext);
    }

    public JaxRsContext contextOrNew() {
        return context().orElse(providers.resolveNotNull(JaxRsContextFactory.class).provides(context));
    }
}
