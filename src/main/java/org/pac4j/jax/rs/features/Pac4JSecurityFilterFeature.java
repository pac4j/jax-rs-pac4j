package org.pac4j.jax.rs.features;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.filter.SecurityFilter;

/**
 * 
 * TODO Normally we would want to inject the request directly in one of the {@link ContainerRequestFilter}, but
 * https://java.net/jira/browse/JERSEY-3167 prevents this because we can't make them implement {@link Feature}. This is
 * why we need this Feature in order to handle the injection when we want to register a {@link SecurityFilter} as a
 * global {@link Feature}.
 * 
 * @author vnoel
 * @since 1.0.0
 *
 */
public class Pac4JSecurityFilterFeature implements Feature {

    /**
     * Note: this is a proxy that is injected and it will map to the correct request during filtering
     * 
     */
    @Context
    private HttpServletRequest request;

    private final Config config;

    private final Boolean skipResponse;

    private final String authorizers;

    private final String clients;

    private final String matchers;

    private final Boolean multiProfile;

    public Pac4JSecurityFilterFeature(Config config) {
        this(config, null, null, null, null, null);
    }

    public Pac4JSecurityFilterFeature(Config config, Boolean skipResponse, String authorizers, String clients,
            String matchers, Boolean multiProfile) {
        this.config = config;
        this.skipResponse = skipResponse;
        this.authorizers = authorizers;
        this.clients = clients;
        this.matchers = matchers;
        this.multiProfile = multiProfile;
    }

    @Override
    public boolean configure(FeatureContext context) {
        final SecurityFilter filter = new SecurityFilter(request, config);
        filter.setAuthorizers(authorizers);
        filter.setClients(clients);
        filter.setMatchers(matchers);
        filter.setMultiProfile(multiProfile);
        filter.setSkipResponse(skipResponse);
        context.register(filter);
        return true;
    }
}
