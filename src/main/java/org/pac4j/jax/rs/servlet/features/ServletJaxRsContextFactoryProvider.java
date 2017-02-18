package org.pac4j.jax.rs.servlet.features;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.pac4j.core.config.Config;
import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider;
import org.pac4j.jax.rs.servlet.pac4j.ServletJaxRsContext;

/**
 * Extends {@link JaxRsContextFactoryProvider} to support any servlet-based container and its session manager (i.e.,
 * pac4j indirect clients will work, contrary than with {@link JaxRsContextFactoryProvider}).
 * 
 * @see JaxRsContextFactoryProvider
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public class ServletJaxRsContextFactoryProvider extends JaxRsContextFactoryProvider {

    @Context
    private HttpServletRequest request;

    public ServletJaxRsContextFactoryProvider(Config config) {
        super(config);
    }

    @Override
    public JaxRsContextFactory getContext(Class<?> type) {
        assert request != null;
        return context -> new ServletJaxRsContext(providers, context, config.getSessionStore(),  request);
    }
}
