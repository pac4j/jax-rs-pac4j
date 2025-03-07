package org.pac4j.jax.rs.filters;

import java.io.IOException;
import java.util.Collection;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Providers;

import org.pac4j.core.adapter.FrameworkAdapter;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityGrantedAccessAdapter;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsFrameworkParameters;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager.Pac4JSecurityContext;

/**
 *
 * TODO this is missing a way to influence URL's prefix for the used clients and
 * authorizers (this is a pac4j limitation)
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter extends AbstractFilter {

    private SecurityLogic securityLogic;

    private String clients;

    private String authorizers;

    private String matchers;

    public SecurityFilter(Providers providers) {
        super(providers);
    }

    @Override
    protected void filter(Config config, ContainerRequestContext requestContext) throws IOException {
        FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);
        JaxRsFrameworkParameters frameworkParameters = new JaxRsFrameworkParameters(providers, requestContext);

        // Note: basically, there is two possible outcomes:
        // either the access is granted or there was an error or a redirect!
        // For the former, we do nothing (see SecurityGrantedAccessOutcome comments)
        // For the later, we interpret the error and abort the request using jax-rs
        // abstractions
        buildLogic(config).perform(config, new SecurityGrantedAccessOutcome(), clients, authorizers, matchers, frameworkParameters);
    }

    protected SecurityLogic buildLogic(Config config) {
        if (securityLogic != null) {
            return securityLogic;
        } else if (config.getSecurityLogic() != null) {
            return config.getSecurityLogic();
        } else {
            return new DefaultSecurityLogic();
        }
    }

    public SecurityLogic getSecurityLogic() {
        return securityLogic;
    }

    public void setSecurityLogic(SecurityLogic securityLogic) {
        this.securityLogic = securityLogic;
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

    private static class SecurityGrantedAccessOutcome implements SecurityGrantedAccessAdapter {
        @Override
        public Object adapt(WebContext context, SessionStore sessionStore, Collection<UserProfile> profiles) throws Exception {
            if (context instanceof JaxRsContext jaxRsContext) {
                SecurityContext original = jaxRsContext.getRequestContext().getSecurityContext();
                jaxRsContext.getRequestContext()
                    .setSecurityContext(new Pac4JSecurityContext(original, jaxRsContext, sessionStore, profiles));
            }
            return null;
        }
    }
}
