package org.pac4j.jax.rs.helpers;

import java.util.Collection;
import java.util.Optional;

import org.pac4j.core.profile.AnonymousProfile;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;

/**
 * @author Victor Noel
 * @since 2.2.0
 * @deprecated will be removed in jax-rs-pac4j 3.0.0, will use {@link ProfileHelper} from pac4j 3.0.0 instead
 */
@Deprecated
public class ProfilesHelper {

    private ProfilesHelper() {
    }

    public static <U extends CommonProfile> Optional<U> flatIntoOneProfile(final Collection<U> profiles) {
        return profiles.stream().filter(p -> p != null && !(p instanceof AnonymousProfile)).findFirst();
    }
}
