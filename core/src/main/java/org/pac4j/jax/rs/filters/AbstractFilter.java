package org.pac4j.jax.rs.filters;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Providers;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.jax.rs.helpers.ProvidersContext;
import org.pac4j.jax.rs.helpers.RequestJaxRsContext;
import org.pac4j.jax.rs.pac4j.JaxRsContext;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public abstract class AbstractFilter implements ContainerRequestFilter, ContainerResponseFilter {

    protected Boolean skipResponse;

    private final Providers providers;

    public AbstractFilter(Providers providers) {
        this.providers = providers;
    }

    protected Config getConfig() {
        return new ProvidersContext(providers).resolveNotNull(Config.class);
    }

    protected SessionStore getSessionStore() {
        return new ProvidersContext(providers).resolveNotNull(SessionStore.class);
    }

    protected abstract void filter(JaxRsContext context) throws IOException;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        filter(new RequestJaxRsContext(providers, requestContext).contextOrNew());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        // in case the filter aborts the request, we never arrive here, but if it is not
        // aborted
        // there is case when pac4j sets things on the response, this is the role of
        // this method.
        // unfortunately, if skipResponse is used, we can't do that because pac4j
        // considers
        // its abort response in the same way as the normal response
        if (skipResponse == null || !skipResponse) {
            new RequestJaxRsContext(providers, requestContext).contextOrNew().getResponseHolder()
                    .populateResponse(responseContext);
        }
    }

    /**
     * Prefer to set a specific {@link HttpActionAdapter} on the {@link Config}
     * instead of overriding this method.
     *
     * @param config the security configuration
     *
     * @return an {@link HttpActionAdapter}
     */
    protected HttpActionAdapter adapter(Config config) {

        final HttpActionAdapter adapter;
        if (config.getHttpActionAdapter() != null) {
            adapter = config.getHttpActionAdapter();
        } else {
            adapter = JaxRsHttpActionAdapter.INSTANCE;
        }

        return (code, context) -> {
            if (skipResponse == null || !skipResponse) {
                adapter.adapt(code, context);
            }
            return null;
        };
    }

    public Boolean isSkipResponse() {
        return skipResponse;
    }

    /**
     * Note that if this is set to <code>true</code>, this will also disable the
     * effects of {@link Authorizer} and such that set things on the HTTP response!
     * Use with caution!
     *
     * @param skipResponse If set to <code>true</code>, the pac4j response, such as
     *                     redirect, will be skipped (the annotated method will be
     *                     executed instead).
     */
    public void setSkipResponse(Boolean skipResponse) {
        this.skipResponse = skipResponse;
    }
}
