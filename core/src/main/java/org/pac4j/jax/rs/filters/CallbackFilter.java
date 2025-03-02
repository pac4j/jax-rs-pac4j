package org.pac4j.jax.rs.filters;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.ext.Providers;

import org.pac4j.core.config.Config;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.DefaultCallbackLogic;
import org.pac4j.jax.rs.pac4j.JaxRsFrameworkParameters;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Priority(Priorities.AUTHORIZATION)
public class CallbackFilter extends AbstractFilter {

    private CallbackLogic callbackLogic;

    private String defaultUrl;

    private Boolean renewSession;

    private String defaultClient;

    public CallbackFilter(Providers providers) {
        super(providers);
    }

    @Override
    protected void filter(Config config, ContainerRequestContext requestContext) throws IOException {
        JaxRsFrameworkParameters frameworkParameters = new JaxRsFrameworkParameters(providers, requestContext);
        buildLogic(config).perform(config, getAbsolutePath(requestContext, defaultUrl, false), renewSession, defaultClient, frameworkParameters);
    }

    protected CallbackLogic buildLogic(Config config) {
        if (callbackLogic != null) {
            return callbackLogic;
        } else if (config.getCallbackLogic() != null) {
            return config.getCallbackLogic();
        } else {
            return new DefaultCallbackLogic();
        }
    }

    public CallbackLogic getCallbackLogic() {
        return callbackLogic;
    }

    public void setCallbackLogic(CallbackLogic callbackLogic) {
        this.callbackLogic = callbackLogic;
    }

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public Boolean isRenewSession() {
        return renewSession;
    }

    public void setRenewSession(Boolean renewSession) {
        this.renewSession = renewSession;
    }

    public String getDefaultClient() {
        return defaultClient;
    }

    public void setDefaultClient(String defaultClient) {
        this.defaultClient = defaultClient;
    }
}
