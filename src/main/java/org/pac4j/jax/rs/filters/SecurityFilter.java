package org.pac4j.jax.rs.filters;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Providers;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.authorization.checker.AuthorizationChecker;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityGrantedAccessAdapter;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.jax.rs.pac4j.JaxRsConfig;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager.Pac4JSecurityContext;

/**
 * 
 * TODO this is missing a way to influence URL's prefix for the used clients and authorizers (this is a pac4j
 * limitation)
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter extends AbstractFilter {

    private SecurityLogic<Object, JaxRsContext> securityLogic;

    private String clients;

    private String authorizers;

    private String matchers;

    private Boolean multiProfile;

    public SecurityFilter(Providers providers) {
        super(providers);
    }

    @Override
    protected void filter(JaxRsContext context) throws IOException {

        Config config = getConfig();

        String cs;
        // no client was set
        if (clients == null && config instanceof JaxRsConfig) {
            cs = ((JaxRsConfig) config).getDefaultClients();
        } else {
            cs = clients;
        }

        // Note: basically, there is two possible outcomes:
        // either the access is granted or there was an error or a redirect!
        // For the former, we do nothing (see SecurityGrantedAccessOutcome comments)
        // For the later, we interpret the error and abort the request using jax-rs abstractions
        SecurityLogic<Object, JaxRsContext> logic = buildLogic(config);
        AuthorizationCheckerWrapper wrapper = null;
        if (logic instanceof DefaultSecurityLogic) {
            wrapper = new AuthorizationCheckerWrapper(((DefaultSecurityLogic) logic).getAuthorizationChecker());
            ((DefaultSecurityLogic) logic).setAuthorizationChecker(wrapper);
        }
        logic.perform(context, config, new SecurityGrantedAccessOutcome(wrapper), adapter(config), cs, authorizers,
                matchers, multiProfile);
    }

    protected SecurityLogic<Object, JaxRsContext> buildLogic(Config config) {
        if (securityLogic != null) {
            return securityLogic;
        } else if (config.getSecurityLogic() != null) {
            return config.getSecurityLogic();
        } else {
            DefaultSecurityLogic<Object, JaxRsContext> logic = new DefaultSecurityLogic<>();
            logic.setProfileManagerFactory(JaxRsProfileManager::new);
            return logic;
        }
    }

    public String getClients() {
        return clients;
    }

    public void setClients(String clients) {
        this.clients = clients;
    }

    public String getAuthorizers() {
        return authorizers;
    }

    public void setAuthorizers(String authorizers) {
        this.authorizers = authorizers;
    }

    public String getMatchers() {
        return matchers;
    }

    public void setMatchers(String matchers) {
        this.matchers = matchers;
    }

    public boolean getMultiProfile() {
        return multiProfile;
    }

    public void setMultiProfile(Boolean multiProfile) {
        this.multiProfile = multiProfile;
    }

    public SecurityLogic<Object, JaxRsContext> getSecurityLogic() {
        return securityLogic;
    }

    public void setSecurityLogic(SecurityLogic<Object, JaxRsContext> securityLogic) {
        this.securityLogic = securityLogic;
    }

    /**
     * @deprecated this will be useless with pac4j 3.0.0 since the profiles are passed to the
     *             {@link SecurityGrantedAccessAdapter}
     */
    @Deprecated
    private static class AuthorizationCheckerWrapper implements AuthorizationChecker {

        private final AuthorizationChecker wrapped;

        public AuthorizationCheckerWrapper(AuthorizationChecker wrapped) {
            this.wrapped = wrapped;
        }

        private List<CommonProfile> profiles = null;

        @Override
        public boolean isAuthorized(WebContext context, List<CommonProfile> profiles, String authorizerNames,
                Map<String, Authorizer> authorizersMap) throws HttpAction {
            this.profiles = profiles;
            return this.wrapped.isAuthorized(context, profiles, authorizerNames, authorizersMap);
        }

    }

    private static class SecurityGrantedAccessOutcome implements SecurityGrantedAccessAdapter<Object, JaxRsContext> {

        private AuthorizationCheckerWrapper wrapper;

        public SecurityGrantedAccessOutcome(AuthorizationCheckerWrapper wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        public Object adapt(JaxRsContext context, Object... parameters) {
            SecurityContext original = context.getRequestContext().getSecurityContext();

            Optional<Collection<CommonProfile>> profiles = wrapper != null ? Optional.ofNullable(wrapper.profiles)
                    : Optional.empty();
            context.getRequestContext().setSecurityContext(new Pac4JSecurityContext(original, context, profiles));
            return null;
        }
    }
}
