package org.pac4j.jax.rs.filters;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Providers;

import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityGrantedAccessAdapter;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.profile.CommonProfile;
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

        // Note: basically, there is two possible outcomes:
        // either the access is granted or there was an error or a redirect!
        // For the former, we do nothing (see SecurityGrantedAccessOutcome comments)
        // For the later, we interpret the error and abort the request using jax-rs abstractions
        buildLogic(config).perform(context, config, new SecurityGrantedAccessOutcome(), adapter(config), clients,
                authorizers, matchers, multiProfile);
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

    private static class SecurityGrantedAccessOutcome implements SecurityGrantedAccessAdapter<Object, JaxRsContext> {
        @Override
        public Object adapt(JaxRsContext context, Collection<CommonProfile> profiles, Object... parameters) {
            SecurityContext original = context.getRequestContext().getSecurityContext();
            context.getRequestContext().setSecurityContext(new Pac4JSecurityContext(original, context, profiles));
            return null;
        }
    }
}
