package org.pac4j.jax.rs.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.SecurityContext;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.pac4j.core.context.DefaultAuthorizers;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager.Pac4JSecurityContext;

@Path("/containerSpecific")
public class RestEasyResource {

    @POST
    @Path("/context")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = DefaultAuthorizers.IS_AUTHENTICATED)
    public String directContext() {
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
}
