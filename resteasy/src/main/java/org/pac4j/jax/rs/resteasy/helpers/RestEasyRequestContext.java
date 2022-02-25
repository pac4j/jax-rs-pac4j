package org.pac4j.jax.rs.resteasy.helpers;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.core.interception.PreMatchContainerRequestContext;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.pac4j.jax.rs.helpers.RequestJaxRsContext;
import org.pac4j.jax.rs.helpers.RequestPac4JSecurityContext;

/**
 * @author Victor Noel
 * @since 2.2.0
 */
public class RestEasyRequestContext extends RequestJaxRsContext {

    public RestEasyRequestContext(Providers providers) {
        this(providers, ResteasyProviderFactory.getContextData(HttpRequest.class));
    }

    public RestEasyRequestContext(Providers providers, HttpRequest request) {
        super(providers,
                new RequestPac4JSecurityContext(ResteasyProviderFactory.getContextData(SecurityContext.class)).context()
                        // if we went through a pac4j security filter
                        .map(sc -> sc.getContext().getRequestContext())
                        // if not, we create a new ContainerRequestContext
                        .orElse(new PreMatchContainerRequestContext(request, new ContainerRequestFilter[] {}, null)));
    }
}
