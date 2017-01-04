package org.pac4j.jax.rs;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JProfile;
import org.pac4j.jax.rs.annotations.Pac4JProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JSecurity;

/**
 * This contains only session-less interactions
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Path("/")
public class TestResource {

    private final Authorizer<CommonProfile> IS_AUTHENTICATED_AUTHORIZER = new IsAuthenticatedAuthorizer<>();

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
    @Path("defaultDirect")
    @Pac4JSecurity(authorizers = "isAuthenticated")
    public String defaultDirect() {
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
    @Path("directInjectManager")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = "isAuthenticated", skipResponse = true)
    public String directInjectManager(@Pac4JProfileManager ProfileManager<CommonProfile> pm) throws HttpAction {
        if (pm != null) {
            // pm.isAuthorized is relying on the session...
            if (IS_AUTHENTICATED_AUTHORIZER.isAuthorized(null, pm.getAll(false))) {
                return "ok";
            } else {
                return "fail";
            }
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
