package org.pac4j.jax.rs.servlet.pac4j;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Providers;

import org.pac4j.core.util.CommonHelper;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsSessionStore;

/**
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class ServletJaxRsContext extends JaxRsContext {

    private final HttpServletRequest request;

    public ServletJaxRsContext(Providers providers, ContainerRequestContext requestContext,
            JaxRsSessionStore sessionStore, HttpServletRequest request) {
        super(providers, requestContext, sessionStore != null ? sessionStore : new ServletSessionStore());
        CommonHelper.assertNotNull("request", request);
        this.request = request;
    }
    
    public HttpServletRequest getRequest() {
        return request;
    }

    @Override
    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }
    
    @Override
    public String getRequestParameter(String name) {
        // simpler implementation
        return request.getParameter(name);
    }
    
    @Override
    public Map<String, String[]> getRequestParameters() {
        // simpler implementation
        return request.getParameterMap();
    }

}
