package org.pac4j.jax.rs.resources;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Providers;

import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.context.session.SessionStore;
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

    @POST
    @Path("/sessionstore")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = DefaultAuthorizers.IS_AUTHENTICATED)
    public String directSessionStore() {
        SessionStore sessionStore = new ProvidersContext(providers).resolveNotNull(SessionStore.class);
        if (sessionStore != null) {
            return "ok";
        } else {
            return "fail";
        }
    }
}
