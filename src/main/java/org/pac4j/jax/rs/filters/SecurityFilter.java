package org.pac4j.jax.rs.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Providers;

import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityGrantedAccessAdapter;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager;

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

    private static final DefaultSecurityLogic<Object, JaxRsContext> DEFAULT_LOGIC = new DefaultSecurityLogic<>();

    static {
        DEFAULT_LOGIC.setProfileManagerFactory(JaxRsProfileManager::new);
    }

    private SecurityLogic<Object, JaxRsContext> securityLogic;

    private String clients;

    private String authorizers;

    private String matchers;

    private Boolean multiProfile;

    public SecurityFilter(Providers providers, Config config) {
        super(providers, config);
    }

    @Override
    protected void filter(JaxRsContext context) throws IOException {
        SecurityLogic<Object, JaxRsContext> sl;

        if (securityLogic != null) {
            sl = securityLogic;
        } else if (config.getSecurityLogic() != null) {
            sl = config.getSecurityLogic();
        } else {
            sl = DEFAULT_LOGIC;
        }

        // Note: basically, there is two possible outcomes:
        // either the access is granted or there was an error or a redirect!
        // For the former, we do nothing (see SecurityGrantedAccessOutcome comments)
        // For the later, we interpret the error and abort the request using jax-rs abstractions
        sl.perform(context, config, SecurityGrantedAccessOutcome.INSTANCE, adapter(), clients, authorizers, matchers,
                multiProfile);
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
}

enum SecurityGrantedAccessOutcome implements SecurityGrantedAccessAdapter<Object, JaxRsContext> {
    INSTANCE;

    @Override
    public Object adapt(JaxRsContext context, Object... parameters) throws Throwable {
        // nothing specific to do, because SecurityGrantedAccessAdapter is meant
        // to be used in a chain of servlet filters but JAX-RS does not do things like that
        return null;
    }

}
