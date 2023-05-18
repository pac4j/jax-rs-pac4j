package org.pac4j.jax.rs.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Path("/class")
@Pac4JSecurity(clients = "DirectFormClient", authorizers = DefaultAuthorizers.IS_AUTHENTICATED)
public class TestClassLevelResource {

    @GET
    @Path("no")
    @Pac4JSecurity(ignore = true)
    public String get() {
        return "ok";
    }

    @POST
    @Path("direct")
    public String direct() {
        return "ok";
    }
}
