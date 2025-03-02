package org.pac4j.jax.rs.grizzly.pac4j;

import org.glassfish.grizzly.http.server.Request;
import org.pac4j.jax.rs.helpers.RequestJaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsContext;

/**
 *
 * Notice: there is often chances that the JAX-RS implementation will read the
 * input stream of the request when it arrives, and after that, it becomes
 * impossible for Grizzly to read it. In particular this means that
 * {@link Request#getParameter(String)} won't be able to return FORM parameters.
 * This is why we don't override
 * {@link JaxRsContext#getRequestParameter(String)} to use the Grizzly
 * implementation.
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class GrizzlyJaxRsContext extends JaxRsContext {

    private final Request request;

    public GrizzlyJaxRsContext(RequestJaxRsContext requestJaxRsContext, Request request) {
        super(requestJaxRsContext);
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    @Override
    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }
}
