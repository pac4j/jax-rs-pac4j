package org.pac4j.jax.rs.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.WithContentAction;
import org.pac4j.core.exception.http.WithLocationAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.jax.rs.pac4j.JaxRsContext;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.1.1
 *
 */
public class DefaultJaxRsHttpActionAdapter implements HttpActionAdapter {

    public static final DefaultJaxRsHttpActionAdapter INSTANCE = new DefaultJaxRsHttpActionAdapter();

    @Override
    public Object adapt(final HttpAction action, final WebContext context) {
        if (action != null && context instanceof JaxRsContext jaxRsContext) {
            if(isSkipResponse(jaxRsContext.getRequestContext())) {
                return null;
            }
            final int code = action.getCode();
            jaxRsContext.getAbortBuilder().status(code);
            jaxRsContext.getResponseHolder().setResponseStatus(code);
            if (action instanceof WithLocationAction) {
                final WithLocationAction withLocationAction = (WithLocationAction) action;
                context.setResponseHeader(HttpConstants.LOCATION_HEADER, withLocationAction.getLocation());
            } else if (action instanceof WithContentAction) {
                final String content = ((WithContentAction) action).getContent();
                jaxRsContext.getAbortBuilder().entity(content);
                jaxRsContext.getResponseHolder().writeResponseContent(content);
            }
            Response response = jaxRsContext.getAbortBuilder().build();
            jaxRsContext.getRequestContext().abortWith(response);
            return null;
        }

        throw new TechnicalException("No action provided");
    }

    private boolean isSkipResponse(ContainerRequestContext requestContext) {
        Object skipResponse = requestContext.getProperty("skipResponse");
        return skipResponse == null || !Boolean.parseBoolean(skipResponse.toString());
    }
}
