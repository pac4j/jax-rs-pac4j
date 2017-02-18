package org.pac4j.jax.rs.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Providers;

import org.pac4j.core.config.Config;
import org.pac4j.core.engine.DefaultLogoutLogic;
import org.pac4j.core.engine.LogoutLogic;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager;

/**
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Priority(Priorities.AUTHORIZATION)
public class LogoutFilter extends AbstractFilter {

    private static final DefaultLogoutLogic<Object, JaxRsContext> DEFAULT_LOGIC = new DefaultLogoutLogic<>();

    static {
        DEFAULT_LOGIC.setProfileManagerFactory(JaxRsProfileManager::new);
    }
    
    private LogoutLogic<Object, JaxRsContext> logoutLogic;

    private String defaultUrl;

    private String logoutUrlPattern;
    
    private Boolean localLogout;
    
    private Boolean destroySession;
    
    private Boolean centralLogout;

    public LogoutFilter(Providers providers, Config config) {
        super(providers, config);
    }

    @Override
    protected void filter(JaxRsContext context) throws IOException {

        LogoutLogic<Object, JaxRsContext> ll;

        if (logoutLogic != null) {
            ll = logoutLogic;
        } else if (config.getLogoutLogic() != null) {
            ll = config.getLogoutLogic();
        } else {
            ll = DEFAULT_LOGIC;
        }

        ll.perform(context, config, adapter(), context.getAbsolutePath(defaultUrl, false),
                context.getAbsolutePath(logoutUrlPattern, false), localLogout, destroySession, centralLogout);
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

    public LogoutLogic<Object, JaxRsContext> getLogoutLogic() {
        return logoutLogic;
    }

    public void setLogoutLogic(LogoutLogic<Object, JaxRsContext> applicationLogoutLogic) {
        this.logoutLogic = applicationLogoutLogic;
    }
}
