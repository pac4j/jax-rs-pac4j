package org.pac4j.jax.rs.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultSecurityLogic;
import org.pac4j.core.engine.SecurityGrantedAccessAdapter;
import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.util.CommonHelper;

/**
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class SecurityFilter extends AbstractFilter {

    private SecurityLogic<Object, JaxRsContext> securityLogic = new DefaultSecurityLogic<>();

    private String clients;

    private String authorizers;

    private String matchers;

    private Boolean multiProfile;

    public SecurityFilter(HttpServletRequest request, Config config) {
        super(request, config);
    }

    @Override
    protected void filter(JaxRsContext context) throws IOException {
        CommonHelper.assertNotNull("securityLogic", securityLogic);

        // Note: basically, there is two possible outcomes:
        // either the access is granted or there was an error or a redirect!
        // For the former, we do nothing (see SecurityGrantedAccessOutcome comments)
        // For the later, we interpret the error and abort the request using jax-rs abstractions
        securityLogic.perform(context, config, SecurityGrantedAccessOutcome.INSTANCE, adapter(), clients, authorizers,
                matchers, multiProfile);
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
        // nothing specific to do, because SecurityGrantedAccessAdapter is meant to be used in a chain of servlet
        // filters but JAX-RS does not do things like that
        return null;
    }

}