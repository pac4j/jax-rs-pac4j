package org.pac4j.jax.rs;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.annotations.Pac4JCallback;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

/**
 * This contains session-based interactions
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Path("/session")
public class TestSessionResource extends TestResource {

    @GET
    @Path("/logged")
    @Pac4JSecurity(clients = "FormClient", authorizers = "isAuthenticated")
    public String logged() {
        return "ok";
    }

    @GET
    @Path("/inject")
    @Pac4JSecurity(clients = "FormClient", authorizers = "isAuthenticated")
    public String inject(@Pac4JProfile CommonProfile profile) {
        if (profile != null) {
            return "ok";
        } else {
            return "error";
        }
    }

    @POST
    @Path("/login")
    // TODO apparently we need to disable session renewal because grizzly
    // send 2 JSESSIONID if not...
    @Pac4JCallback(defaultUrl = "/logged", renewSession = false)
    public void login() {
        // nothing
    }
}
