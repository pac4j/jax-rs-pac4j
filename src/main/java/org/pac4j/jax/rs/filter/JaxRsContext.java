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
 * TODO it would be even better to be completely servlet-agnostic but it could
 * need some redesigning of {@link WebContext} for this to happen...
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
        // Note: expiry is not in servlet and is meant to be superseeded by
        // max-age, so we simply make it null
        getAbortBuilder().cookie(new NewCookie(cookie.getName(), cookie.getValue(), cookie.getPath(),
                cookie.getDomain(), cookie.getVersion(), cookie.getComment(), cookie.getMaxAge(), null,
                cookie.isSecure(), cookie.isHttpOnly()));
    }

    /**
     * This gives us what is after the baseURI, which consists of the servlet
     * context + the servlet mapping
     */
    @Override
    public String getPath() {
        return "/" + requestContext.getUriInfo().getPath();
    }

    public String getAbsolutePath(String relativePath) {
        String urlPrefix = requestContext.getUriInfo().getBaseUri().getPath();
        if (relativePath == null) {
            return null;
        } else if (relativePath.startsWith("/")) {
            // urlPrefix already contains the ending /
            return urlPrefix + relativePath.substring(1);
        } else {
            return relativePath;
        }
    }
}
