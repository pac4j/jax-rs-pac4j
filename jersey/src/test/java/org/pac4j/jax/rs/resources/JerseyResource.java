package org.pac4j.jax.rs.resources;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Providers;

import org.pac4j.core.context.DefaultAuthorizers;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider.JaxRsContextFactory;
import org.pac4j.jax.rs.helpers.ProvidersContext;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager.Pac4JSecurityContext;

@Path("/containerSpecific")
public class JerseyResource {

    @Context
    private Providers providers;

    @Inject
    private ContainerRequestContext requestContext;

    @POST
    @Path("/securitycontext")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = DefaultAuthorizers.IS_AUTHENTICATED)
    public String directSecurityContext() {
        // Note: SecurityContext injected via @Context can't be cast
        SecurityContext context = requestContext.getSecurityContext();
        if (context != null) {
            if (context instanceof Pac4JSecurityContext) {
                return "ok";
            } else {
                return "fail";
            }
        } else {
            return "error";
        }
    }

    @POST
    @Path("/context")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = DefaultAuthorizers.IS_AUTHENTICATED)
    public String directContext() {
        JaxRsContext context = new ProvidersContext(providers).resolveNotNull(JaxRsContextFactory.class)
                .provides(requestContext);
        if (context != null) {
            return "ok";
        } else {
            return "fail";
        }
    }
}
