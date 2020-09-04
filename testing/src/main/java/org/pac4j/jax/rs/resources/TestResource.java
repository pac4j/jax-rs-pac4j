package org.pac4j.jax.rs.resources;

import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.authorizer.DefaultAuthorizers;
import org.pac4j.core.authorization.authorizer.IsAuthenticatedAuthorizer;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.matching.matcher.DefaultMatchers;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.Pac4JPrincipal;
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
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = DefaultAuthorizers.IS_AUTHENTICATED)
    public String direct() {
        return "ok";
    }

    @POST
    @Path("defaultDirect")
    @Pac4JSecurity(authorizers = DefaultAuthorizers.IS_AUTHENTICATED)
    public String defaultDirect() {
        return "ok";
    }

    @POST
    @Path("directInject")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = DefaultAuthorizers.IS_AUTHENTICATED)
    public String directInject(@Pac4JProfile CommonProfile profile) {
        if (profile != null) {
            return "ok";
        } else {
            return "error";
        }
    }

    @POST
    @Path("directContext")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = DefaultAuthorizers.IS_AUTHENTICATED)
    public String directContext(@Context SecurityContext context) {
        if (context != null) {
            if (context.getUserPrincipal() instanceof Pac4JPrincipal) {
                return "ok";
            } else {
                return "fail";
            }
        } else {
            return "error";
        }
    }

    @GET
    @Path("directInjectNoAuth")
    public String directInjectNoAuth(@Pac4JProfile CommonProfile profile) {
        if (profile != null) {
            return "ok";
        } else {
            return "error";
        }
    }

    @POST
    @Path("directInjectManager")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = DefaultAuthorizers.IS_AUTHENTICATED, skipResponse = true)
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
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = DefaultAuthorizers.IS_AUTHENTICATED, skipResponse = true)
    public String directInjectSkip(@Pac4JProfile Optional<CommonProfile> profile) {
        if (profile.isPresent()) {
            return "ok";
        } else {
            return "fail";
        }
    }

    @POST
    @Path("directResponseHeadersSet")
    @Pac4JSecurity(clients = "DirectFormClient", authorizers = DefaultAuthorizers.IS_AUTHENTICATED, matchers = DefaultMatchers.NOSNIFF)
    public String directResponseHeadersSet() {
        return "ok";
    }
}
