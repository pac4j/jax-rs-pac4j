package org.pac4j.jax.rs.pac4j;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import javax.ws.rs.core.SecurityContext;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jax.rs.helpers.ProfilesHelper;
import org.pac4j.jax.rs.helpers.RequestPac4JSecurityContext;

/**
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class JaxRsProfileManager extends ProfileManager<CommonProfile> {

    public JaxRsProfileManager(JaxRsContext context) {
        super(context);
    }

    @Override
    public void logout() {
        super.logout();

        new RequestPac4JSecurityContext((JaxRsContext) this.context).context().ifPresent(c -> c.principal = null);
    }

    public static class Pac4JSecurityContext implements SecurityContext {

        private final SecurityContext original;

        /**
         * If this is null, it means we are not logged in!
         */
        private Principal principal;

        private final Optional<Collection<CommonProfile>> profiles;

        private final JaxRsContext context;

        public Pac4JSecurityContext(SecurityContext original, JaxRsContext context,
                Optional<Collection<CommonProfile>> profiles) {
            this.original = original;
            this.context = context;
            this.profiles = profiles;
            this.principal = profiles.flatMap(ps -> ProfilesHelper.flatIntoOneProfile(ps).map(PrincipalImpl::new))
                    .orElse(null);
        }

        public Optional<Collection<CommonProfile>> getProfiles() {
            if (principal != null) {
                return profiles.map(ps -> Collections.unmodifiableCollection(ps));
            } else {
                return Optional.empty();
            }
        }

        public JaxRsContext getContext() {
            // even after logout we can access the context
            return this.context;
        }

        @Override
        public Principal getUserPrincipal() {
            if (principal != null) {
                return principal;
            } else {
                return original != null ? original.getUserPrincipal() : null;
            }
        }

        @Override
        public boolean isUserInRole(String role) {
            if (principal != null) {
                return profiles.map(ps -> ps.stream().anyMatch(p -> p.getRoles().contains(role))).orElse(false);
            } else {
                return original != null && original.isUserInRole(role);
            }
        }

        @Override
        public boolean isSecure() {
            return original != null && original.isSecure();
        }

        @Override
        public String getAuthenticationScheme() {
            if (principal != null) {
                return "PAC4J";
            } else {
                return original != null ? original.getAuthenticationScheme() : null;
            }
        }
    }

    /**
     * @deprecated will be removed in jax-rs-pac4j 3.0.0, will use Pac4JPrincipal from pac4j 3.0.0 instead
     */
    @Deprecated
    public static class PrincipalImpl implements Principal {

        private final String name;

        public PrincipalImpl(CommonProfile profile) {
            String username = profile.getUsername();
            if (CommonHelper.isNotBlank(username)) {
                this.name = username;
            } else {
                this.name = profile.getId();
            }
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getName());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final PrincipalImpl principal = (PrincipalImpl) o;
            return CommonHelper.areEquals(this.getName(), principal.getName());
        }

        @Override
        public String toString() {
            return CommonHelper.toString(this.getClass(), "profileId", this.name);
        }
    }
}
