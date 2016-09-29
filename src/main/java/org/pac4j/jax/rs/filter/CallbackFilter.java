package org.pac4j.jax.rs.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.pac4j.core.config.Config;
import org.pac4j.core.engine.CallbackLogic;
import org.pac4j.core.engine.J2ERenewSessionCallbackLogic;
import org.pac4j.core.util.CommonHelper;

/**
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Priority(Priorities.AUTHENTICATION)
public class CallbackFilter implements ContainerRequestFilter {

    private CallbackLogic<Object, JaxRsContext> callbackLogic = new J2ERenewSessionCallbackLogic<>();

    private final HttpServletRequest request;

    private final Config config;

    private String defaultUrl;

    private Boolean multiProfile;

    private Boolean renewSession;

    private Boolean skipResponse;

    public CallbackFilter(HttpServletRequest request, Config config) {
        this.request = request;
        this.config = config;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        CommonHelper.assertNotNull("callbackLogic", callbackLogic);
        CommonHelper.assertNotNull("config", config);

        final JaxRsContext context = new JaxRsContext(request, config.getSessionStore(), requestContext);

        final JaxRsHttpActionAdapter adapter;
        if (skipResponse != null && skipResponse) {
            adapter = JaxRsHttpActionAdapter.SKIP;
        } else {
            adapter = JaxRsHttpActionAdapter.ADAPT;
        }

        callbackLogic.perform(context, config, adapter, defaultUrl, multiProfile, renewSession);
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

    public Boolean isSkipResponse() {
        return skipResponse;
    }

    /**
     * @param skipResponse
     *            If set to <code>true</code>, the pac4j response, such as redirect, will be skipped (the annotated
     *            method will be executed instead).
     */
    public void setSkipResponse(Boolean skipResponse) {
        this.skipResponse = skipResponse;
    }
}