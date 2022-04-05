package org.pac4j.jax.rs.servlet.features;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider;
import org.pac4j.jax.rs.servlet.pac4j.ServletJaxRsContext;

/**
 * Extends {@link JaxRsContextFactoryProvider} to support any servlet-based
 * container
 * 
 * @see JaxRsContextFactoryProvider
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class ServletJaxRsContextFactoryProvider extends JaxRsContextFactoryProvider {

    @Context
    private HttpServletRequest httpRequest;

    @Override
    public JaxRsContextFactory getContext(Class<?> type) {
        return context -> new ServletJaxRsContext(getProviders(), context, httpRequest);
    }
}
