package org.pac4j.jax.rs.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.SecurityContext;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager.Pac4JSecurityContext;

@Path("/containerSpecific")
public class RestEasyResource {

    @POST
    @Path("/securitycontext")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = DefaultAuthorizers.IS_AUTHENTICATED)
    public String directSecurityContext() {
        // Note: SecurityContext injected via @Context can't be cast
        SecurityContext context = ResteasyProviderFactory.getContextData(SecurityContext.class);
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
        SecurityContext scontext = ResteasyProviderFactory.getContextData(SecurityContext.class);
        if (scontext != null && scontext instanceof Pac4JSecurityContext) {
            JaxRsContext context = ((Pac4JSecurityContext) scontext).getContext();
            if (context != null) {
                return "ok";
            } else {
                return "fail";
            }
        } else {
            return "error";
        }
    }

    @POST
    @Path("/sessionstore")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = DefaultAuthorizers.IS_AUTHENTICATED)
    public String directSessionStore() {
        SecurityContext scontext = ResteasyProviderFactory.getContextData(SecurityContext.class);
        if (scontext != null && scontext instanceof Pac4JSecurityContext) {
            SessionStore sessionStore = ((Pac4JSecurityContext) scontext).getSessionStore();
            if (sessionStore != null) {
                return "ok";
            } else {
                return "fail";
            }
        } else {
            return "error";
        }
    }
}
