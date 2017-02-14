package org.pac4j.jax.rs.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Providers;

import org.pac4j.core.config.Config;
import org.pac4j.core.engine.ApplicationLogoutLogic;
import org.pac4j.jax.rs.pac4j.JaxRsApplicationLogoutLogic;
import org.pac4j.jax.rs.pac4j.JaxRsContext;

/**
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Priority(Priorities.AUTHORIZATION)
public class ApplicationLogoutFilter extends AbstractFilter {

    private static final JaxRsApplicationLogoutLogic<JaxRsContext> DEFAULT_LOGIC = new JaxRsApplicationLogoutLogic<>();

    private ApplicationLogoutLogic<Object, JaxRsContext> applicationLogoutLogic;

    private String defaultUrl;

    private String logoutUrlPattern;

    public ApplicationLogoutFilter(Providers providers, Config config) {
        super(providers, config);
    }

    @Override
    protected void filter(JaxRsContext context) throws IOException {

        ApplicationLogoutLogic<Object, JaxRsContext> apl;

        if (applicationLogoutLogic != null) {
            apl = applicationLogoutLogic;
        } else if (config.getApplicationLogoutLogic() != null) {
            apl = config.getApplicationLogoutLogic();
        } else {
            apl = DEFAULT_LOGIC;
        }

        apl.perform(context, config, adapter(), context.getAbsolutePath(defaultUrl, false),
                context.getAbsolutePath(logoutUrlPattern, false));
    }

    public String getDefaultUrl() {
        return this.defaultUrl;
    }

    public void setDefaultUrl(final String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public String getLogoutUrlPattern() {
        return logoutUrlPattern;
    }

    public void setLogoutUrlPattern(String logoutUrlPattern) {
        this.logoutUrlPattern = logoutUrlPattern;
    }

    public ApplicationLogoutLogic<Object, JaxRsContext> getApplicationLogoutLogic() {
        return applicationLogoutLogic;
    }

    public void setApplicationLogoutLogic(ApplicationLogoutLogic<Object, JaxRsContext> applicationLogoutLogic) {
        this.applicationLogoutLogic = applicationLogoutLogic;
    }
}
