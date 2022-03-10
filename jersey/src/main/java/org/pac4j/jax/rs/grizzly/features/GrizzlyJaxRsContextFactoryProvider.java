package org.pac4j.jax.rs.grizzly.features;

import javax.inject.Provider;
import javax.ws.rs.core.Context;

import org.glassfish.grizzly.http.server.Request;
import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider;
import org.pac4j.jax.rs.grizzly.pac4j.GrizzlyJaxRsContext;

/**
 * 
 * Extends {@link JaxRsContextFactoryProvider} to support the Grizzly container
 * (without the need for servlet support)
 * 
 * @see JaxRsContextFactoryProvider
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class GrizzlyJaxRsContextFactoryProvider extends JaxRsContextFactoryProvider {

    @Context
    protected Provider<Request> requestProvider;

    @Override
    public JaxRsContextFactory getContext(Class<?> type) {
        Request request = requestProvider.get();
        assert request != null;
        return context -> new GrizzlyJaxRsContext(getProviders(), context, request);
    }
}