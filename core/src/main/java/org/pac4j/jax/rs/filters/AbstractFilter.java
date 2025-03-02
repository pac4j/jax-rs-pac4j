package org.pac4j.jax.rs.filters;

import java.io.IOException;
import java.net.URI;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Providers;

import org.pac4j.core.adapter.FrameworkAdapter;
import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.jax.rs.helpers.ProvidersContext;
import org.pac4j.jax.rs.helpers.RequestJaxRsContext;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public abstract class AbstractFilter implements ContainerRequestFilter, ContainerResponseFilter {

    protected Boolean skipResponse;

    protected final Providers providers;

    public AbstractFilter(Providers providers) {
        this.providers = providers;
    }

    protected Config getConfig() {
        return new ProvidersContext(providers).resolveNotNull(Config.class);
    }

    protected SessionStore getSessionStore() {
        return new ProvidersContext(providers).resolveNotNull(SessionStore.class);
    }

    protected abstract void filter(Config config, ContainerRequestContext requestContext) throws IOException;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Config config = getConfig();
        FrameworkAdapter.INSTANCE.applyDefaultSettingsIfUndefined(config);
        filter(config, requestContext);
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

    protected String getAbsolutePath(ContainerRequestContext requestContext, String relativePath, boolean full) {
        if (relativePath == null) {
            return null;
        } else if (relativePath.startsWith("/")) {
            URI baseUri = requestContext.getUriInfo().getBaseUri();
            String urlPrefix;
            if (full) {
                urlPrefix = baseUri.toString();
            } else {
                urlPrefix = baseUri.getPath();
            }
            // urlPrefix already contains the ending /
            return urlPrefix + relativePath.substring(1);
        } else {
            return relativePath;
        }
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
