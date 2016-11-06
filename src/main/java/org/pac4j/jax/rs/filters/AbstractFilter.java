package org.pac4j.jax.rs.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import org.pac4j.core.config.Config;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.jax.rs.features.JaxRsContextFactoryProvider.JaxRsContextFactory;
import org.pac4j.jax.rs.pac4j.JaxRsContext;

/**
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public abstract class AbstractFilter implements ContainerRequestFilter {

    protected Boolean skipResponse;

    protected final Config config;

    private final Providers providers;

    public AbstractFilter(Providers providers, Config config) {
        this.providers = providers;
        this.config = config;
    }

    protected abstract void filter(JaxRsContext context) throws IOException;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        ContextResolver<JaxRsContextFactory> contextResolver = providers.getContextResolver(JaxRsContextFactory.class,
                MediaType.WILDCARD_TYPE);
        JaxRsContextFactory contextFactory = contextResolver.getContext(JaxRsContextFactory.class);
        JaxRsContext context = contextFactory.provides(requestContext);

        CommonHelper.assertNotNull("contextProvider", context);

        filter(context);
    }

    protected HttpActionAdapter<Object, JaxRsContext> adapter() {
        return (code, context) -> {
            if (skipResponse == null || !skipResponse) {
                context.getRequestContext().abortWith(context.getAbortBuilder().build());
            }
            return null;
        };
    }

    public Boolean isSkipResponse() {
        return skipResponse;
    }

    /**
     * @param skipResponse
     *            If set to <code>true</code>, the pac4j response, such as redirect, will be skipped (the annotated
     *            method will be executed instead).
     */
    public void setSkipResponse(Boolean skipResponse) {
        this.skipResponse = skipResponse;
    }
}
