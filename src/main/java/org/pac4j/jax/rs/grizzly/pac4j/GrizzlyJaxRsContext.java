package org.pac4j.jax.rs.grizzly.pac4j;

import java.io.InputStream;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Providers;

import org.glassfish.grizzly.http.server.Request;
import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsSessionStore;
import org.pac4j.jax.rs.servlet.pac4j.ServletJaxRsContext;

/**
 * 
 * Note that even though {@link Request} gives access to the parameters including Form ones as we do in
 * {@link ServletJaxRsContext}, the fact that Jersey read the {@link InputStream} prevents to access them, so we must
 * rely on {@link JaxRsContext} code to access them!
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class GrizzlyJaxRsContext extends JaxRsContext {

    private final Request request;

    public GrizzlyJaxRsContext(Providers providers, ContainerRequestContext requestContext,
            JaxRsSessionStore sessionStore, Request request) {
        super(providers, requestContext, sessionStore != null ? null : new GrizzlySessionStore());
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
