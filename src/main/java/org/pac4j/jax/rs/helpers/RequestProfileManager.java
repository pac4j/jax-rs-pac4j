package org.pac4j.jax.rs.helpers;

import org.pac4j.jax.rs.pac4j.JaxRsProfileManager;

/**
 * @author Victor Noel
 * @since 2.2.0
 */
public class RequestProfileManager {

    private final RequestJaxRsContext context;

    public RequestProfileManager(RequestJaxRsContext context) {
        this.context = context;
    }

    public JaxRsProfileManager profileManager() {
        return new JaxRsProfileManager(context.contextOrNew());
    }
}
