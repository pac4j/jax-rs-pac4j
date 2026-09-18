package org.pac4j.jax.rs.resources;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager.Pac4JSecurityContext;

@Path("/containerSpecific")
public class RestEasyResource {

    @GET
    @Path("/servlet61/redirect")
    public void servlet61Redirect(@Context HttpServletResponse response) throws IOException {
        response.getWriter().write("preserved");
        // This overload requires a Servlet 6.1 implementation, not just the API jar.
        response.sendRedirect("/no", 307, false);
    }

    @POST
    @Path("/securitycontext")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = DefaultAuthorizers.IS_AUTHENTICATED)
    public String directSecurityContext() {
        // Note: SecurityContext injected via @Context can't be cast
        SecurityContext context = ResteasyProviderFactory.getInstance().getContextData(SecurityContext.class);
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
        SecurityContext scontext = ResteasyProviderFactory.getInstance().getContextData(SecurityContext.class);
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
        SecurityContext scontext = ResteasyProviderFactory.getInstance().getContextData(SecurityContext.class);
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
