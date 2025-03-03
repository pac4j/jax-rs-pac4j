package org.pac4j.jax.rs.pac4j;

import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.WebContextFactory;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider;
import org.pac4j.jax.rs.helpers.ProvidersContext;

public class DefaultJaxRsWebContextFactory implements WebContextFactory {

    public static final WebContextFactory INSTANCE = new DefaultJaxRsWebContextFactory();

    @Override
    public WebContext newContext(FrameworkParameters parameters) {
        if(parameters instanceof JaxRsFrameworkParameters jaxRsFrameworkParameters) {
            ProvidersContext providersContext = jaxRsFrameworkParameters.getProvidersContext();
            JaxRsContextFactoryProvider.JaxRsContextFactory contextFactory = providersContext.resolveNotNull(JaxRsContextFactoryProvider.JaxRsContextFactory.class);
            return contextFactory.provides(jaxRsFrameworkParameters.getRequestContext());
        }
        throw new TechnicalException("Wrong framework parameters, expected JAX-RS framework parameters");
    }
}
