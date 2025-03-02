package org.pac4j.jax.rs.filters;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.ext.Providers;

import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.core.engine.LogoutLogic;
import org.pac4j.jax.rs.pac4j.JaxRsFrameworkParameters;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Priority(Priorities.AUTHORIZATION)
public class LogoutFilter extends AbstractFilter {

    private LogoutLogic logoutLogic;

    private String defaultUrl;

    private String logoutUrlPattern;

    private Boolean localLogout;

    private Boolean destroySession;

    private Boolean centralLogout;

    public LogoutFilter(Providers providers) {
        super(providers);
    }

    @Override
    protected void filter(Config config, ContainerRequestContext requestContext) throws IOException {
        JaxRsFrameworkParameters frameworkParameters = new JaxRsFrameworkParameters(providers, requestContext);
        buildLogic(config).perform(config, defaultUrl, getAbsolutePath(requestContext, logoutUrlPattern, false), localLogout, destroySession, centralLogout, frameworkParameters);
    }

    protected LogoutLogic buildLogic(Config config) {
        if (logoutLogic != null) {
            return logoutLogic;
        } else if (config.getLogoutLogic() != null) {
            return config.getLogoutLogic();
        } else {
            return new DefaultLogoutLogic();
        }
    }

    public LogoutLogic getLogoutLogic() {
        return logoutLogic;
    }

    public void setLogoutLogic(LogoutLogic logoutLogic) {
        this.logoutLogic = logoutLogic;
    }

    public String getDefaultUrl() {
        return defaultUrl;
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

    public Boolean getLocalLogout() {
        return localLogout;
    }

    public void setLocalLogout(Boolean localLogout) {
        this.localLogout = localLogout;
    }

    public Boolean getDestroySession() {
        return destroySession;
    }

    public void setDestroySession(Boolean destroySession) {
        this.destroySession = destroySession;
    }

    public Boolean getCentralLogout() {
        return centralLogout;
    }

    public void setCentralLogout(Boolean centralLogout) {
        this.centralLogout = centralLogout;
    }
}
