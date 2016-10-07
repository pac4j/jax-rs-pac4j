package org.pac4j.jax.rs.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.pac4j.core.config.Config;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.util.CommonHelper;

/**
 * 
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
@Priority(Priorities.AUTHENTICATION)
public abstract class AbstractFilter implements ContainerRequestFilter {

    protected final HttpServletRequest request;

    protected final Config config;

    protected Boolean skipResponse;

    public AbstractFilter(HttpServletRequest request, Config config) {
        this.request = request;
        this.config = config;
    }

    protected abstract void filter(JaxRsContext context) throws IOException;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        CommonHelper.assertNotNull("config", config);
        CommonHelper.assertNotNull("request", request);

        final JaxRsContext context = new JaxRsContext(request, config.getSessionStore(), requestContext);

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
