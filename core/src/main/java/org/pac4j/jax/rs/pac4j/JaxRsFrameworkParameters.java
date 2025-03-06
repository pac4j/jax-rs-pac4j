package org.pac4j.jax.rs.pac4j;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.ext.Providers;
import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.jax.rs.helpers.ProvidersContext;

/**
 * Implementation of the {@link FrameworkParameters} interface for a JAX-RS environment.
 * <p>
 * This class encapsulates parameters related to the JAX-RS framework, including
 * providers, their context, and the current request context.
 * </p>
 *
 * @since 7.0.0
 */
public class JaxRsFrameworkParameters implements FrameworkParameters {

    /**
     * JAX-RS providers instance
     */
    private final Providers providers;

    /**
     * JAX-RS context derived from the providers
     */
    private final ProvidersContext providersContext;

    /**
     *  Container request context representing the current HTTP request
     */
    private final ContainerRequestContext requestContext;

    /**
     * Constructs a new {@code JaxRsFrameworkParameters} instance
     *
     * @param providers      the JAX-RS providers used to locate various components (e.g., message body readers/writers)
     * @param requestContext the container request context for the current HTTP request
     */
    public JaxRsFrameworkParameters(Providers providers, ContainerRequestContext requestContext) {
        this.providers = providers;
        this.providersContext = new ProvidersContext(providers);
        this.requestContext = requestContext;
    }

    /**
     * Returns the JAX-RS providers instance.
     *
     * @return the providers instance
     */
    public Providers getProviders() {
        return providers;
    }

    /**
     * Returns the context derived from the JAX-RS providers.
     *
     * @return the providers context
     */
    public ProvidersContext getProvidersContext() {
        return providersContext;
    }

    /**
     * Returns the container request context for the current HTTP request.
     *
     * @return the container request context
     */
    public ContainerRequestContext getRequestContext() {
        return requestContext;
    }
}
