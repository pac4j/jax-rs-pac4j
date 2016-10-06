package org.pac4j.jax.rs.filter;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;

/**
 * TODO it would be even better to be completely servlet-agnostic but it could need some redesigning of
 * {@link WebContext} for this to happen...
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class JaxRsContext extends J2EContext {

    private final ContainerRequestContext requestContext;

    private ResponseBuilder abortResponse = null;

    public JaxRsContext(HttpServletRequest request, SessionStore<J2EContext> sessionStore,
            ContainerRequestContext requestContext) {
        super(request, null, sessionStore);
        this.requestContext = requestContext;
    }

    public ContainerRequestContext getRequestContext() {
        return requestContext;
    }

    public ResponseBuilder getAbortBuilder() {
        if (abortResponse == null) {
            abortResponse = Response.ok();
        }
        return abortResponse;
    }

    @Override
    public void writeResponseContent(String content) {
        getAbortBuilder().entity(content);
    }

    @Override
    public void setResponseStatus(int code) {
        getAbortBuilder().status(code);
    }

    @Override
    public void setResponseHeader(String name, String value) {
        getAbortBuilder().header(name, value);
    }

    @Override
    public void setResponseContentType(String content) {
        getAbortBuilder().type(content);
    }

    @Override
    public void addResponseCookie(Cookie cookie) {
        // Note: expiry is not in servlet and is meant to be superseeded by max-age, so we simply make it null
        getAbortBuilder().cookie(new NewCookie(cookie.getName(), cookie.getValue(), cookie.getPath(),
                cookie.getDomain(), cookie.getVersion(), cookie.getComment(), cookie.getMaxAge(), null,
                cookie.isSecure(), cookie.isHttpOnly()));
    }

    /**
     * When using JAX-RS over a Servlet container, the path info is what we are interested in.
     * 
     * The context path is the base path of the servlet context that contains the servlet for the JAX-RS implementation.
     * 
     * The servlet path is the base path of the servlet itself.
     * 
     * And the path info is what is left after that.
     * 
     * Since we are working only with URIs inside the JAX-RS implementation, we only need the path info!
     */
    @Override
    public String getPath() {
        return getRequest().getPathInfo();
    }

}