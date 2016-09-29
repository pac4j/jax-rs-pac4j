package org.pac4j.jax.rs.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.pac4j.core.config.Config;
import org.pac4j.core.engine.ApplicationLogoutLogic;
import org.pac4j.core.engine.DefaultApplicationLogoutLogic;
import org.pac4j.core.util.CommonHelper;

/**
 * 
 * TODO Support filtering on response in order to do the logout once the method has been executed.
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Priority(Priorities.AUTHENTICATION)
public class ApplicationLogoutFilter implements ContainerRequestFilter {

    private ApplicationLogoutLogic<Object, JaxRsContext> applicationLogoutLogic = new DefaultApplicationLogoutLogic<>();

    private final HttpServletRequest request;

    private final Config config;

    private String defaultUrl;

    private String logoutUrlPattern;

    private Boolean skipResponse;

    public ApplicationLogoutFilter(HttpServletRequest request, Config config) {
        this.request = request;
        this.config = config;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        CommonHelper.assertNotNull("applicationLogoutLogic", applicationLogoutLogic);
        CommonHelper.assertNotNull("config", config);
        CommonHelper.assertNotNull("request", request);

        final JaxRsContext context = new JaxRsContext(request, config.getSessionStore(), requestContext);

        final JaxRsHttpActionAdapter adapter;
        if (skipResponse != null && skipResponse) {
            adapter = JaxRsHttpActionAdapter.SKIP;
        } else {
            adapter = JaxRsHttpActionAdapter.ADAPT;
        }

        applicationLogoutLogic.perform(context, config, adapter, defaultUrl, logoutUrlPattern);
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