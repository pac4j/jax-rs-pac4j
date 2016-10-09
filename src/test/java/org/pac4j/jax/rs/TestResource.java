package org.pac4j.jax.rs;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

/**
 * This contains only session-less interactions
 * 
 * @author vnoel
 *
 */
@Path("/")
public class TestResource {

    @GET
    @Path("no")
    public String get() {
        return "ok";
    }

    @POST
    @Path("direct")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = "isAuthenticated")
    public String direct() {
        return "ok";
    }

    @POST
    @Path("directInject")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = "isAuthenticated")
    public String directInject(@Pac4JProfile(readFromSession = false) CommonProfile profile) {
        if (profile != null) {
            return "ok";
        } else {
            return "error";
        }
    }

    @POST
    @Path("directInjectSkip")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = "isAuthenticated", skipResponse = true)
    public String directInjectSkip(@Pac4JProfile(readFromSession = false) CommonProfile profile) {
        if (profile != null) {
            return "ok";
        } else {
            return "fail";
        }
    }
}
