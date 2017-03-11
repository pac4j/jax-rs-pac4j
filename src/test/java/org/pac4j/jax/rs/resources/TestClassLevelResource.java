package org.pac4j.jax.rs.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.pac4j.jax.rs.annotations.Pac4JSecurity;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Path("/class")
@Pac4JSecurity(clients = "DirectFormClient", authorizers = "isAuthenticated")
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
