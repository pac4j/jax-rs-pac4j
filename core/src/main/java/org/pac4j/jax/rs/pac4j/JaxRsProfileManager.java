package org.pac4j.jax.rs.pac4j;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import javax.ws.rs.core.SecurityContext;

import org.pac4j.core.profile.*;
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

        private final Collection<UserProfile> profiles;

        private final JaxRsContext context;

        public Pac4JSecurityContext(SecurityContext original, JaxRsContext context,
                Collection<UserProfile> profiles) {
            this.original = original;
            this.context = context;
            this.profiles = profiles;
            this.principal = ProfileHelper.flatIntoOneProfile(profiles).map(Pac4JPrincipal::new).orElse(null);
        }

        public Optional<Collection<UserProfile>> getProfiles() {
            if (principal != null) {
                return Optional.of(Collections.unmodifiableCollection(profiles));
            } else if (original instanceof Pac4JSecurityContext) {
                return ((Pac4JSecurityContext) original).getProfiles();
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
                return profiles.stream().anyMatch(p -> p.getRoles().contains(role));
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
}
