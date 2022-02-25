package org.pac4j.jax.rs.filters;

import javax.ws.rs.core.Response;

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
public class JaxRsHttpActionAdapter implements HttpActionAdapter {

    public static final JaxRsHttpActionAdapter INSTANCE = new JaxRsHttpActionAdapter();

    @Override
    public Object adapt(final HttpAction action, final WebContext context) {
        if (action != null && context instanceof JaxRsContext) {
            JaxRsContext jaxRsContext = (JaxRsContext) context;
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
}
