package org.pac4j.jax.rs.features;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Providers;

import org.pac4j.jax.rs.filters.SecurityFilter;

/**
 * 
 * Registers a global {@link SecurityFilter} to protect all the URLs served by the JAX-RS runtime.
 * 
 * TODO Normally we would want to register directly {@link SecurityFilter} but because of
 * https://java.net/jira/browse/JERSEY-3167, we can't make it implement {@link Feature}. This is why we need this
 * {@link Feature} in order to handle the injection when we want to register a {@link SecurityFilter} as a global
 * {@link Feature}.
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class Pac4JSecurityFilterFeature implements Feature {

    @Context
    private Providers providers;

    private final Boolean skipResponse;

    private final String authorizers;

    private final String clients;

    private final String matchers;

    private final Boolean multiProfile;

    public Pac4JSecurityFilterFeature() {
        this(null, null, null, null, null);
    }

    public Pac4JSecurityFilterFeature(Boolean skipResponse, String authorizers, String clients, String matchers,
            Boolean multiProfile) {
        this.skipResponse = skipResponse;
        this.authorizers = authorizers;
        this.clients = clients;
        this.matchers = matchers;
        this.multiProfile = multiProfile;
    }

    @Override
    public boolean configure(FeatureContext context) {
        final SecurityFilter filter = new SecurityFilter(providers);
        filter.setAuthorizers(authorizers);
        filter.setClients(clients);
        filter.setMatchers(matchers);
        filter.setMultiProfile(multiProfile);
        filter.setSkipResponse(skipResponse);
        context.register(filter);
        return true;
    }
}
