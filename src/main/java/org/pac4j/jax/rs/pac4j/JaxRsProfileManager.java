package org.pac4j.jax.rs.pac4j;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.core.util.CommonHelper;

/**
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class JaxRsProfileManager extends ProfileManager<CommonProfile> {

    public JaxRsProfileManager(WebContext context) {
        super(context);

        ContainerRequestContext requestContext = getJaxRsContext().getRequestContext();
        SecurityContext original = requestContext.getSecurityContext();
        if (!(original instanceof Pac4JSecurityContext)) {
            requestContext.setSecurityContext(new Pac4JSecurityContext(original));
        }
    }

    protected JaxRsContext getJaxRsContext() {
        return (JaxRsContext) this.context;
    }

    @Override
    protected LinkedHashMap<String, CommonProfile> retrieveAll(boolean readFromSession) {
        LinkedHashMap<String, CommonProfile> profiles = super.retrieveAll(readFromSession);

        SecurityContext securityContext = getJaxRsContext().getRequestContext().getSecurityContext();
        if (securityContext instanceof Pac4JSecurityContext) {
            ((Pac4JSecurityContext) securityContext).setPrincipal(profiles);
        }

        return profiles;
    }

    @Override
    public void logout() {
        super.logout();

        SecurityContext securityContext = getJaxRsContext().getRequestContext().getSecurityContext();
        if (securityContext instanceof Pac4JSecurityContext) {
            ((Pac4JSecurityContext) securityContext).unsetPrincipal();
        }
    }

    private static class Pac4JSecurityContext implements SecurityContext {

        private final SecurityContext original;

        private PrincipalImpl principal;

        public Pac4JSecurityContext(SecurityContext original) {
            this.original = original;
        }

        public void setPrincipal(LinkedHashMap<String, CommonProfile> profiles) {
            if (profiles != null && !profiles.isEmpty()) {
                principal = new PrincipalImpl(profiles);
            }
        }

        public void unsetPrincipal() {
            this.principal = null;
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
                return principal.getRoles().contains(role);
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

    public static class PrincipalImpl implements Principal {

        private final CommonProfile profile;

        private final Set<String> roles;

        public PrincipalImpl(LinkedHashMap<String, CommonProfile> profiles) {
            Optional<CommonProfile> optProfile = ProfileHelper.flatIntoOneProfile(profiles);
            if (!optProfile.isPresent()) {
                throw new IllegalArgumentException();
            }
            this.profile = optProfile.get();
            Set<String> rs = new HashSet<>();
            profiles.values().stream().forEach(p -> rs.addAll(p.getRoles()));
            this.roles = Collections.unmodifiableSet(rs);
        }

        public CommonProfile getProfile() {
            return profile;
        }

        @Override
        public String getName() {
            return this.profile.getId();
        }

        public Set<String> getRoles() {
            return roles;
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
            return CommonHelper.toString(this.getClass(), "profile", this.profile);
        }
    }
}
