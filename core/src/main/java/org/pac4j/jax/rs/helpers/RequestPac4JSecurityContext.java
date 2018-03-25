package org.pac4j.jax.rs.helpers;

import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

import org.pac4j.jax.rs.pac4j.JaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsProfileManager.Pac4JSecurityContext;

/**
 * @author Victor Noel
 * @since 2.2.0
 */
public class RequestPac4JSecurityContext {

    private final SecurityContext securityContext;

    public RequestPac4JSecurityContext(JaxRsContext context) {
        this(context.getRequestContext());
    }

    public RequestPac4JSecurityContext(ContainerRequestContext request) {
        this(request.getSecurityContext());
    }

    public RequestPac4JSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    public Optional<Pac4JSecurityContext> context() {
        if (securityContext instanceof Pac4JSecurityContext) {
            return Optional.of((Pac4JSecurityContext) securityContext);
        } else {
            return Optional.empty();
        }
    }
}
