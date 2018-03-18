package org.pac4j.jax.rs.helpers;

import java.util.Optional;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager.Pac4JSecurityContext;

/**
 * @author Victor Noel
 * @since 2.2.0
 */
public class RequestCommonProfile {

    private final RequestPac4JSecurityContext context;

    public RequestCommonProfile(RequestPac4JSecurityContext context) {
        this.context = context;
    }

    public Optional<CommonProfile> profile() {
        return context.context()
                .flatMap(Pac4JSecurityContext::getProfiles)
                .flatMap(ps -> ProfileHelper.flatIntoOneProfile(ps));
    }
}