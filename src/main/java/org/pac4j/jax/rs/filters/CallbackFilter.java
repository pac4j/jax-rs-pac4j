package org.pac4j.jax.rs.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Providers;

import org.pac4j.core.config.Config;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsRenewSessionCallbackLogic;

/**
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Priority(Priorities.AUTHORIZATION)
public class CallbackFilter extends AbstractFilter {

    private static final JaxRsRenewSessionCallbackLogic<JaxRsContext> DEFAULT_LOGIC = new JaxRsRenewSessionCallbackLogic<>();

    private CallbackLogic<Object, JaxRsContext> callbackLogic;

    private String defaultUrl;

    private Boolean multiProfile;

    private Boolean renewSession;

    public CallbackFilter(Providers providers, Config config) {
        super(providers, config);
    }

    @Override
    protected void filter(JaxRsContext context) throws IOException {

        CallbackLogic<Object, JaxRsContext> cl;

        if (callbackLogic != null) {
            cl = callbackLogic;
        } else if (config.getCallbackLogic() != null) {
            cl = config.getCallbackLogic();
        } else {
            cl = DEFAULT_LOGIC;
        }

        cl.perform(context, config, adapter(), context.getAbsolutePath(defaultUrl, false), multiProfile, renewSession);
    }

    public CallbackLogic<Object, JaxRsContext> getCallbackLogic() {
        return callbackLogic;
    }

    public void setCallbackLogic(CallbackLogic<Object, JaxRsContext> callbackLogic) {
        this.callbackLogic = callbackLogic;
    }

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public boolean isMultiProfile() {
        return multiProfile;
    }

    public void setMultiProfile(Boolean multiProfile) {
        this.multiProfile = multiProfile;
    }

    public boolean isRenewSession() {
        return renewSession;
    }

    public void setRenewSession(Boolean renewSession) {
        this.renewSession = renewSession;
    }
}
