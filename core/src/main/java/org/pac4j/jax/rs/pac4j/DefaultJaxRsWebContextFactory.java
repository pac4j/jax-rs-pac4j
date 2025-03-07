package org.pac4j.jax.rs.pac4j;

import jakarta.ws.rs.container.ContainerRequestContext;
import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.WebContextFactory;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider;
import org.pac4j.jax.rs.helpers.ProvidersContext;

/**
 * Default implementation of {@link WebContextFactory} for JAX-RS environments.
 * <p>
 *  This factory creates a {@link WebContext} from JAX-RS framework parameters.
 *  It expects the provided {@link FrameworkParameters} to be an instance of
 *  {@link JaxRsFrameworkParameters} in order to extract the necessary context.
 * </p>
 *
 * @since 7.0.0
 */
public class DefaultJaxRsWebContextFactory implements WebContextFactory {

    public static final WebContextFactory INSTANCE = new DefaultJaxRsWebContextFactory();

    /**
     * Creates a new {@link WebContext} using the provided framework parameters.
     * <p>
     * The creation of the context is done by resolving the {@link JaxRsContextFactoryProvider.JaxRsContextFactory}
     * from the current {@link ProvidersContext}. The {@link JaxRsContextFactoryProvider.JaxRsContextFactory} provides
     * a new {@link WebContext} based on the {@link ContainerRequestContext} contained in the
     * {@link JaxRsFrameworkParameters}.
     * </p>
     *
     * @param parameters framework parameters used to create the {@link WebContext}
     * @return New instance of {@link WebContext}
     * @throws TechnicalException if the provided parameters are not an instance of {@link JaxRsFrameworkParameters}
     */
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
